package com.example.player_service.events;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PlayerUpdatedEvent(String eventType, UUID auctionId, UUID playerId, Instant updatedAt, List<UserDetails> participants) {
}
