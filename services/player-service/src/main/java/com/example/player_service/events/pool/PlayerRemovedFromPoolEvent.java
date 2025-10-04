package com.example.player_service.events.pool;

import java.time.Instant;
import java.util.UUID;

public record PlayerRemovedFromPoolEvent(UUID auctionId, UUID playerId, Instant removedAt) {}

