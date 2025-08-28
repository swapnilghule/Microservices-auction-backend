
package com.example.auction_service;

import com.example.auction_service.domain.*;
import com.example.auction_service.domain.state.AuctionState;
import com.example.auction_service.domain.state.CreatedState;
import com.example.auction_service.dto.AuctionDTO;
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
import java.util.UUID;
import java.util.logging.ErrorManager;

@Service
public class AuctionServiceApp {

  private final OutBoxRepository outboxRepository;

  private final ObjectMapper mapper;
  private final KafkaTemplate<String,String> kafkaTemplate;
  private final String topic;


  private final AuctionRepository auctionRepository;

  public AuctionServiceApp(AuctionRepository repository, OutBoxRepository outboxRepository, ObjectMapper mapper, KafkaTemplate<String,String> kafkaTemplate, Environment env) {
    this.auctionRepository = repository;
    this.outboxRepository = outboxRepository;
    this.mapper=mapper;
    this.kafkaTemplate=kafkaTemplate;
    this.topic=env.getProperty("app.topics.auctions");
  }


  @Transactional
  public Auction create(String name) throws JsonProcessingException {
    UUID id = UUID.randomUUID();
    Auction auction = new Auction();
    auction.setId(UUID.randomUUID());
    auction.setName(name);
    auction.setStatus(AuctionStatus.CREATED);
    auction.setCreatedAt(Instant.now());
    String payload = mapper.writeValueAsString(new AuctionDTO(auction));
    outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "Auction", auction.getId(), "AuctionCreated","{}", payload, auction.getCreatedAt(), false));
    return auctionRepository.save(auction);
  }

  @Transactional
  public Auction start(UUID id) throws JsonProcessingException {
    Auction auction= auctionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Auction Not found with id:"+ id));
    AuctionState handler = auction.getStateHandler();
    handler.start(auction);
    String payload = mapper.writeValueAsString(new AuctionDTO(auction));
    auctionRepository.save(auction);
    outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "Auction", auction.getId(), "AuctionStarted","{}", payload, auction.getCreatedAt(), false));
    return auction;
  }

  @Transactional
  public Auction close(UUID id) throws JsonProcessingException{
    Auction auction= auctionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Auction Not found with id:"+ id));
    AuctionState handler= auction.getStateHandler();
    handler.close(auction);
    String payload = mapper.writeValueAsString(new AuctionDTO(auction));
    auctionRepository.save(auction);
    outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "Auction", auction.getId(), "AuctionClosed","{}", payload, auction.getCreatedAt(), false));
    return auction;
  }

  @Scheduled(fixedDelay = 1000)
  public void publishOutboxBatch() {
    List<OutboxEvent> events = outboxRepository.findBatchToPublish();

    for (OutboxEvent e : events) {
      try {
        // use event.id as Kafka key for idempotency
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




//  public void close(UUID id){
//    agg(id).close();
//  }
//  public void place(UUID id, UUID playerId, UUID userId, BigDecimal amount){
//    agg(id).placeBid(playerId, userId, amount);
//  }
//  public String status(UUID id){
//    return agg(id).status();
//
//  }
//  public void setBase(UUID id, UUID playerId, BigDecimal base) {
//    agg(id).setBasePrice(playerId, base);
//  }

//  private AuctionAggregate agg(UUID id) {
//    AuctionAggregate a = auctions.get(id);
//    if (a == null) throw new IllegalArgumentException("Auction not found: " + id);
//    return a;
//  }
}
