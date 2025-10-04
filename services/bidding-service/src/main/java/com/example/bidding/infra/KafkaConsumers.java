package com.example.bidding.infra;



import com.example.bidding.BiddingService;
import com.example.bidding.dto.PlayerUpdatedEvent;
import com.example.common.events.component.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class KafkaConsumers {
    private final ObjectMapper objectMapper;

    private final BiddingService biddingService;

    private final JsonUtils jsonUtils;
    private final Map<UUID, UUID> activePlayers = new ConcurrentHashMap<>();


    public KafkaConsumers(ObjectMapper objectMapper, BiddingService biddingService, JsonUtils jsonUtils) {
        this.objectMapper = objectMapper;
        this.biddingService = biddingService;

        this.jsonUtils = jsonUtils;
    }

    @KafkaListener(topics = "player-events.v1", groupId = "bidding-service")
    public void consume(String payload) {
        try {
            String NormalizedPayload = jsonUtils.normalize(payload);
            System.out.println("Normalized Paylaod"+ NormalizedPayload);
            JsonNode node = objectMapper.readTree(NormalizedPayload);
            String eventType = node.get("eventType").asText();
            UUID auctionId = UUID.fromString(node.get("auctionId").asText());
            System.out.println("eventType: "+ eventType);
            System.out.println("AuctionID: "+ auctionId);


            PlayerUpdatedEvent event = objectMapper.readValue(NormalizedPayload, PlayerUpdatedEvent.class);

            System.out.println("event: "+ event);


            if ("PlayerUpdated".equals(eventType)) {
                UUID playerId = UUID.fromString(node.get("playerId").asText());
                activePlayers.put(auctionId, playerId);

                log.info("Auction {} is now open for player {}", auctionId, playerId);

                biddingService.startBiddingRound(auctionId, playerId, event.getParticipants());
            } else {
                //    log.debug("Ignoring event type: {}", eventType);
            }
        } catch (Exception e) {
            //    log.error("Error processing event", e);
        }
    }
}
