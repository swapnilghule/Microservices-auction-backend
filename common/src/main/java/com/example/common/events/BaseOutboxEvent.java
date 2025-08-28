package com.example.common.events;

import jakarta.persistence.MappedSuperclass;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
public class BaseOutboxEvent {
    private UUID id;

    private String aggregateType;

    private UUID aggregateId;

    private String eventType;

    private String payload;

    private String headers;

    private Instant occurredAt = Instant.now();

    private boolean published = false;
}
