package com.example.player_service.events;


import java.time.Instant;
import java.util.UUID;

public record PlayerAddedEvent(String eventType,UUID playerId, String name, String role, double basePrice, Instant createdAt) {
    public PlayerAddedEvent(UUID playerId, String name, String role, double basePrice, Instant createdAt) {
        this("PlayerAdded", playerId, name, role, basePrice, createdAt);
    }
}

