package com.example.player_service.infra;

import com.example.player_service.entity.JsonUtils;
import com.example.player_service.events.AuctionReadyEvent;
import com.example.player_service.events.WinnerDeclaredEvent;
import com.example.player_service.services.PlayerPoolService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


import java.util.UUID;

@Slf4j
@Component
public class KafkaConsumers {
    private final ObjectMapper objectMapper;
    private final JsonUtils jsonUtils;
    private final PlayerPoolService service;


    public KafkaConsumers(ObjectMapper objectMapper, JsonUtils jsonUtils, PlayerPoolService service) {
        this.objectMapper = objectMapper;
        this.jsonUtils = jsonUtils;
        this.service = service;
    }


    @KafkaListener(topics = "auction-events.v1", groupId = "player-service")
    public void consume(String payload) {
        try {
            String NormalizedPayload = jsonUtils.normalize(payload);
            System.out.println(NormalizedPayload);

            JsonNode node = objectMapper.readTree(NormalizedPayload);
            String eventType = node.get("eventType").asText();
            UUID auctionId = UUID.fromString(node.get("id").asText());
            System.out.println(eventType);

            AuctionReadyEvent auctionReadyEvent = objectMapper.readValue(NormalizedPayload, AuctionReadyEvent.class);
            auctionReadyEvent.getRegisteredUsers().forEach(u ->
                    System.out.println("User: " + u.getUserId() + ", Purse: " + u.getPurseAmount())
            );
            if ("AuctionReady".equals(eventType)) {
//                  System.out.println("Inside if bcoz eventType is"+ eventType);
//                  System.out.println("Inside If and auction is"+ auctionReadyEvent.getId() +"auction name is :"+ auctionReadyEvent.getName());
                  service.getPoolByAuction(auctionId, auctionReadyEvent.getRegisteredUsers());
            } else {
            //    log.debug("Ignoring event type: {}", eventType);
            }
        } catch (Exception e) {
        //    log.error("Error processing event", e);
        }
    }


    @KafkaListener(topics = "bid-events.v1", groupId = "auction-service")
    public void consumeBid(String payload) {
        try {
            log.info("📥 Received: {}", payload);
            String NormalizedPayload = jsonUtils.normalize(payload);

            JsonNode node = objectMapper.readTree(payload);
            String eventType = node.get("eventType").asText();
            UUID auctionId = UUID.fromString(node.get("auctionId").asText());
            WinnerDeclaredEvent winnerDeclaredEvent = objectMapper.readValue(NormalizedPayload, WinnerDeclaredEvent.class);

            if ("WinnerDeclared".equals(eventType)) {

                service.getPoolByAuction(auctionId, winnerDeclaredEvent.getParticipants());
            } else {
                log.debug("Ignoring event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error(" Error processing event", e);
        }
    }
}
