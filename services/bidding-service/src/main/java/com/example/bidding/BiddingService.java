package com.example.bidding;
import com.example.bidding.domain.Bid; import com.example.bidding.domain.BidRepository; import com.example.bidding.outbox.OutboxEvent; import com.example.bidding.domain.OutboxRepository;
import com.example.common.events.BidPlacedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.ErrorManager;

@Service
public class BiddingService {
  private final BidRepository bidRepository;
  private final OutboxRepository outboxRepository;
  private final ObjectMapper mapper;
  private final KafkaTemplate<String,String> kafkaTemplate;
  private final String topic;

  public BiddingService(BidRepository bidRepository, OutboxRepository outboxRepository, ObjectMapper mapper, KafkaTemplate<String,String> kafkaTemplate, Environment env){
    this.bidRepository=bidRepository;
    this.outboxRepository=outboxRepository;
    this.mapper=mapper;
    this.kafkaTemplate=kafkaTemplate;
    this.topic=env.getProperty("app.topics.bidEvents");
  }

  @Transactional
  public UUID placeBid(UUID auctionId, UUID playerId, UUID userId, BigDecimal amount) throws Exception {
    UUID bidId = UUID.randomUUID(); Instant now = Instant.now();
    bidRepository.save(new Bid(bidId, auctionId, playerId, userId, amount, now));
    String payload = mapper.writeValueAsString(new BidPlacedEvent(bidId, auctionId, playerId, userId, amount.doubleValue(), now));
    outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "BID", bidId, "BidPlaced","{}", payload, now, false));
    return bidId;
  }

  @Scheduled(fixedDelay = 1000)
  public void publishOutboxBatch() {
    List<OutboxEvent> events = outboxRepository.findBatchToPublish();

    for (OutboxEvent e : events) {
      try {
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