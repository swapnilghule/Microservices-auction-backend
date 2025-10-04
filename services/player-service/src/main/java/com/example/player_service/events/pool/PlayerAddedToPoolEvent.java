package com.example.player_service.events.pool;

import java.time.Instant;
import java.util.UUID;

public record PlayerAddedToPoolEvent(UUID auctionId, UUID playerId, Instant createdAt) {}
