
package com.example.auction_service.infra;

import com.example.auction_service.AuctionServiceApp;
import com.example.auction_service.domain.Auction;
import com.example.auction_service.domain.WinnerSummary;
import com.example.auction_service.domain.WinnerSummaryRepository;
import com.example.auction_service.dto.AuctionDTO;
import com.example.auction_service.dto.WinnerDeclaredEvent;
import com.example.common.events.BidPlacedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.example.auction_service.AuctionServiceApp;

import java.util.UUID;

@Slf4j
@Component
public class KafkaConsumers {
    private final AuctionServiceApp auctionAppService;
    private final ObjectMapper objectMapper;
    private final WinnerSummaryRepository repository;


    public KafkaConsumers(AuctionServiceApp auctionAppService, ObjectMapper objectMapper, WinnerSummaryRepository repository) {
        this.auctionAppService = auctionAppService;
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    @KafkaListener(topics = "auction-events.v1", groupId = "auction-service")
    public void consume(String payload) {
        try {
            System.out.println(payload);
            JsonNode node = objectMapper.readTree(payload);
            String eventType = node.get("eventType").asText();
            if ("AuctionStarted".equals(eventType)) {
                AuctionDTO dto = objectMapper.treeToValue(node, AuctionDTO.class);
                UUID auctionId = dto.getId();
                auctionAppService.start(auctionId);
            } else {
                log.debug("Ignoring event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing event", e);
        }
    }

    @KafkaListener(topics = "bid-events.v1", groupId = "auction-service")
    public void consumeBid(String payload) {
        try {
            log.info("📥 Received: {}", payload);
            JsonNode node = objectMapper.readTree(payload);
            String eventType = node.get("eventType").asText();

            if ("WinnerDeclared".equals(eventType)) {
                WinnerDeclaredEvent event = objectMapper.treeToValue(node, WinnerDeclaredEvent.class);

                WinnerSummary summary = new WinnerSummary(
                        event.getBidId(),
                        event.getAuctionId(),
                        event.getPlayerId(),
                        event.getUserId(),
                        event.getAmount(),
                        event.getDeclaredAt()
                );

                repository.save(summary);
                log.info("✅ Winner summary saved for auction {}", event.getAuctionId());
            } else {
                log.debug("Ignoring event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("❌ Error processing event", e);
        }
    }
}
