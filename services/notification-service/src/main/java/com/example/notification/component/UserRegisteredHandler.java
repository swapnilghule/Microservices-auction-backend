package com.example.notification.component;

import com.example.notification.handler.OnEventHandler;
import com.example.notification.service.AuctionRedisService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserRegisteredHandler implements OnEventHandler {

    private final AuctionRedisService redisService;

    public UserRegisteredHandler(AuctionRedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void handle(JsonNode payload) {
        UUID auctionId = UUID.fromString(payload.get("auctionId").asText());
        UUID userId = UUID.fromString(payload.get("userId").asText());
        redisService.addRegisteredUser(auctionId, userId);
        System.out.println("Registered user " + userId + " for auction " + auctionId);
    }
}