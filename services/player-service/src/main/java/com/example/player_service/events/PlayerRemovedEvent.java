package com.example.player_service.events;

import java.time.Instant;
import java.util.UUID;

public record PlayerRemovedEvent(UUID playerId, Instant removedAt) {}
