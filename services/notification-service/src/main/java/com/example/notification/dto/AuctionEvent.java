package com.example.notification.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AuctionEvent {
    private UUID id;
    private String eventType="AuctionStarted"; // "STARTED", "ENDED"
    private Instant timestamp;

    public AuctionEvent(String uuid, Instant now) {
        this.id = UUID.fromString(uuid);
        this.timestamp = now;
    }
}