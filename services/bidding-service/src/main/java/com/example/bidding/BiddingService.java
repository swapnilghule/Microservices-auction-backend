package com.example.bidding;
import com.example.bidding.domain.*;
import com.example.bidding.dto.PlayerUpdatedEvent;
import com.example.bidding.dto.UserDTO;
import com.example.bidding.dto.WinnerDeclaredEvent;
import com.example.bidding.outbox.OutboxEvent;
import com.example.bidding.validation.BidValidator;
import com.example.common.events.BidPlacedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ErrorManager;

@Service
@Slf4j
public class BiddingService {
  private final BidRepository bidRepository;
  private final OutboxRepository outboxRepository;
  private final ObjectMapper mapper;
  private final KafkaTemplate<String,String> kafkaTemplate;

  private final List<BidValidator> validators;
  private final Map<UUID, BiddingRound> activeRounds = new ConcurrentHashMap<>();
  private final Map<UUID, PlayerUpdatedEvent> activePlayers = new ConcurrentHashMap<>();

  private final String topic;

  public BiddingService(BidRepository bidRepository, OutboxRepository outboxRepository, ObjectMapper mapper, KafkaTemplate<String,String> kafkaTemplate, List<BidValidator> validators, Environment env){
    this.bidRepository=bidRepository;
    this.outboxRepository=outboxRepository;
    this.mapper=mapper;
    this.kafkaTemplate=kafkaTemplate;
      this.validators = validators;
      this.topic=env.getProperty("app.topics.bidEvents");
  }

//  @Transactional
//  public UUID placeBid(UUID auctionId, UUID playerId, UUID userId, Integer amount) throws Exception {
//    UUID bidId = UUID.randomUUID(); Instant now = Instant.now();
//    bidRepository.save(new Bid(bidId, auctionId, playerId, userId, amount, now));
//    String payload = mapper.writeValueAsString(new BidPlacedEvent(bidId, auctionId, playerId, userId, amount.doubleValue(), now));
//    outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "BID", bidId, "BidPlaced","{}", payload, now, false));
//    return bidId;
//  }


  @Transactional
  public String placeBid(Bid bid, PlayerUpdatedEvent playerEvent) throws JsonProcessingException {
    UUID auctionId = bid.getAuctionId();
    UUID playerId = playerEvent.getPlayerId();

    if (!bid.getPlayerId().equals(playerId)) {
      return "Invalid bid: You cannot bid on a different player.";
    }

    // Initialize or fetch the round
    BiddingRound round = activeRounds.computeIfAbsent(auctionId, k -> new BiddingRound(playerId, auctionId));

    // Restriction: prevent multiple valid bids from the same user
    boolean alreadyBid = round.getBids().stream()
            .anyMatch(existing -> existing.getUserId().equals(bid.getUserId())
                    && existing.getPlayerId().equals(playerId));

    if (alreadyBid) {
      return "You have already placed a bid for this player.";
    }

    // Find the user from participants in PlayerUpdatedEvent
    UserDTO user = playerEvent.getParticipants().stream()
            .filter(u -> u.getUserId().equals(bid.getUserId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("User not registered for this auction"));

    // Run all validators (BasePrice, PurseAmount, etc.)
    for (BidValidator validator : validators) {
      String error = validator.validate(bid, playerEvent, user.getPurseAmount());
      if (error != null) return error; // fail fast
    }

    // Save bid
    bidRepository.save(bid);

    // Add bid to round
    round.addBid(bid);

    log.info("Comparing round size: {} | Participants size: {}", round.getBids().size(),
            playerEvent.getParticipants() != null ? playerEvent.getParticipants().size() : 0);

    // Check if all participants placed their bids
    if (round.getBids().size() == (playerEvent.getParticipants() != null ? playerEvent.getParticipants().size() : 0)) {
      log.info("Inside round size: {} | Participants size: {}", round.getBids().size(),
              playerEvent.getParticipants() != null ? playerEvent.getParticipants().size() : 0);

      // Ensure participants list is not null
      List<UserDTO> participants = playerEvent.getParticipants() != null ? playerEvent.getParticipants() : List.of();
      finalizeRound(auctionId, round, participants);
    }

    // Save Outbox Event
    String payload = mapper.writeValueAsString(new BidPlacedEvent(
            bid.getId(),
            bid.getAuctionId(),
            bid.getPlayerId(),
            bid.getUserId(),
            bid.getAmount().doubleValue(),
            bid.getCreatedAt()
    ));

    outboxRepository.save(new OutboxEvent(
            UUID.randomUUID(),
            "BID",
            bid.getId(),
            "BidPlaced",
            "{}",
            payload,
            bid.getCreatedAt(),
            false
    ));

    return "Bid accepted";
  }

  public void startBiddingRound(UUID auctionId, UUID playerId, List<UserDTO> participants) {
    PlayerUpdatedEvent event = new PlayerUpdatedEvent(auctionId, playerId, Instant.now(), participants);
    activePlayers.put(auctionId, event);

    // Initialize round state for this auction + player
    BiddingRound round = new BiddingRound(playerId, auctionId);
    activeRounds.put(auctionId, round);

    System.out.println("Bidding round started for player: " + playerId);
  }


  private void finalizeRound(UUID auctionId, BiddingRound round, List<UserDTO> registeredUser) {
    log.info("Finalizing round for player: {}", round.getPlayerId());

    // Ensure participants list is not null
    if (registeredUser == null) {
      registeredUser = List.of();
    }

    Optional<Bid> winningBidOpt = round.getBids().stream()
            .max(Comparator.comparingInt(Bid::getAmount));

    if (winningBidOpt.isEmpty()) {
      log.info("No valid bids found.");
      return;
    }

    Bid winningBid = winningBidOpt.get();

    try {
      WinnerDeclaredEvent winnerEvent = new WinnerDeclaredEvent(
              winningBid.getId(),
              auctionId,
              round.getPlayerId(),
              winningBid.getUserId(),
              winningBid.getAmount(),
              Instant.now(),
              "WinnerDeclared",
              registeredUser
      );

      String payload = mapper.writeValueAsString(winnerEvent);

      outboxRepository.save(new OutboxEvent(
              UUID.randomUUID(),
              "BID",
              winningBid.getId(),
              "WinnerDeclared",
              "{}",
              payload,
              Instant.now(),
              false
      ));

      log.info("Winner declared for player {} : User {} with bid {} | Payload: {}",
              round.getPlayerId(),
              winningBid.getUserId(),
              winningBid.getAmount(),
              payload
      );

    } catch (Exception e) {
      log.error("Error while creating WinnerDeclaredEvent", e);
    }
  }


  private PlayerUpdatedEvent getCurrentPlayer(UUID auctionId) {
    // fetch from DB or state
    return null;
  }

  private List<UserDTO> getParticipants(UUID auctionId) {
    // fetch from DB or cache
    return List.of();
  }

  public PlayerUpdatedEvent getActivePlayerEvent(UUID auctionId) {
    return activePlayers.get(auctionId);
  }

  public BiddingRound getBiddingRound(UUID auctionId) {
    return activeRounds.get(auctionId);
  }

  @Scheduled(fixedDelay = 1000)
  public void publishOutboxBatch() {
    List<OutboxEvent> events = outboxRepository.findBatchToPublish();

    for (OutboxEvent e : events) {
      try {
        System.out.println("Publishing Event: "+ e.getEventType()+"To the Topic"+ topic+"with payload"+ e.getPayload());

        kafkaTemplate.send(topic, e.getEventType(), e.getPayload()).get();
        e.markPublished();
      } catch (Exception ex) {
        ErrorManager log = null;
        log.error("Failed to publish outbox event {}: {}", ex, 1);
      }
    }

    // batch update DB
    outboxRepository.saveAll(events.stream().filter(OutboxEvent::isPublished).toList());
  }
}