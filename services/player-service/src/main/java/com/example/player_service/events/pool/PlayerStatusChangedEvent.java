package com.example.player_service.events.pool;

import java.time.Instant;
import java.util.UUID;

public record PlayerStatusChangedEvent(UUID auctionId, UUID playerId, String status, Instant updatedAt) {}
