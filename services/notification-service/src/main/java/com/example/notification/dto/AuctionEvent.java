package com.example.notification.dto;

import jakarta.annotation.sql.DataSourceDefinition;
import lombok.Data;

import java.util.UUID;

@Data
public class AuctionEvent {
    private UUID auctionId;
    private String eventType; // "STARTED", "ENDED"
    private long timestamp;

    // getters and setters
}