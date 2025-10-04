
package com.example.auction_service;

import com.example.auction_service.domain.*;
import com.example.auction_service.domain.state.AuctionState;
import com.example.auction_service.domain.state.CreatedState;
import com.example.auction_service.dto.AuctionDTO;
import com.example.auction_service.dto.AuctionRegistrationDTO;
import com.example.auction_service.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.TransactionScoped;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.ErrorManager;
import java.util.stream.Collectors;

@Service
public class AuctionServiceApp {

  private final OutBoxRepository outboxRepository;

  private final ObjectMapper mapper;
  private final KafkaTemplate<String,String> kafkaTemplate;
  private final String topic;
  private final AuctionRegistrationRepository registrationRepository;



  private final AuctionRepository auctionRepository;

  public AuctionServiceApp(AuctionRepository repository, OutBoxRepository outboxRepository, ObjectMapper mapper, KafkaTemplate<String,String> kafkaTemplate, Environment env, AuctionRegistrationRepository registrationRepository) {
    this.auctionRepository = repository;
    this.outboxRepository = outboxRepository;
    this.mapper=mapper;
    this.kafkaTemplate=kafkaTemplate;
    this.topic=env.getProperty("app.topics.auctions");
    this.registrationRepository = registrationRepository;
  }


  @Transactional
  public Auction create(String name) throws JsonProcessingException {
    UUID id = UUID.randomUUID();
    Auction auction = new Auction();
    auction.setId(UUID.randomUUID());
    auction.setName(name);
    auction.setStatus(AuctionStatus.CREATED);
    auction.setCreatedAt(Instant.now());
    String payload = mapper.writeValueAsString(new AuctionDTO(auction, "AuctionCreated", null));
    outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "Auction", auction.getId(), "AuctionCreated","{}", payload, auction.getCreatedAt(), false));
    return auctionRepository.save(auction);
  }

  @Transactional
  public Auction start(UUID id) throws JsonProcessingException {
    Auction auction= auctionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Auction Not found with id:"+ id));
    AuctionState handler = auction.getStateHandler();
    handler.start(auction);
    // Fetch registered users
    List<AuctionRegistration> registrations = registrationRepository.findByAuctionId(id);

    List<UserDTO> participants = registrations.stream()
            .map(reg -> new UserDTO(reg.getUserId(), 8000, ""))
            .toList();

    String payload = mapper.writeValueAsString(new AuctionDTO(auction, "AuctionReady", participants));
    auctionRepository.save(auction);
    outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "Auction", auction.getId(), "AuctionReady","{}", payload, auction.getCreatedAt(), false));
    return auction;
  }

  @Transactional
  public Auction close(UUID id) throws JsonProcessingException{
    Auction auction= auctionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Auction Not found with id:"+ id));
    AuctionState handler= auction.getStateHandler();
    handler.close(auction);
    String payload = mapper.writeValueAsString(new AuctionDTO(auction, "AuctionClosed", null));
    auctionRepository.save(auction);
    outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "Auction", auction.getId(), "AuctionClosed","{}", payload, auction.getCreatedAt(), false));
    return auction;
  }

  @Transactional
  public UUID registerUser(UUID auctionId, UUID userId) throws JsonProcessingException {
    // check duplicate
    auctionRepository.findById(auctionId)
            .orElseThrow(() -> new IllegalArgumentException("Auction not found: " + auctionId));

    boolean alreadyRegistered = registrationRepository.findByAuctionIdAndUserId(auctionId, userId).isPresent();
    if (alreadyRegistered) {
      throw new IllegalStateException("User already registered for this auction");
    }

    UUID regId = UUID.randomUUID();
    AuctionRegistration reg = new AuctionRegistration(regId, auctionId, userId);
    registrationRepository.save(reg);

    String payload = mapper.writeValueAsString(
            new AuctionRegistrationDTO(regId, auctionId, userId, Instant.now())
    );
    outboxRepository.save(new OutboxEvent(
            UUID.randomUUID(), "Auction", auctionId, "AuctionUserRegistered",
            "{}", payload, Instant.now(), false
    ));

    return regId;
  }

  @Transactional(readOnly = true)
  public boolean isUserRegistered(UUID auctionId, UUID userId) {
    return registrationRepository.findByAuctionIdAndUserId(auctionId, userId).isPresent();
  }

  @Scheduled(fixedDelay = 1000)
  public void publishOutboxBatch() {
    List<OutboxEvent> events = outboxRepository.findBatchToPublish();

    for (OutboxEvent e : events) {
      try {
        // use event.id as Kafka key for idempotency
        System.out.println("Publishing Event"+ e.getEventType()+"To the Topic"+ topic+"with payload"+ e.getPayload());
        kafkaTemplate.send(topic, e.getEventType(), e.getPayload()).get();
        e.markPublished();
      } catch (Exception ex) {
        // leave unpublished, will retry next cycle
        ErrorManager log = null;
        log.error("Failed to publish outbox event {}: {}", ex, 1);
      }
    }
    // batch update DB
    outboxRepository.saveAll(events.stream().filter(OutboxEvent::isPublished).toList());
  }

}
