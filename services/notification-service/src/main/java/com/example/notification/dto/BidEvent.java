package com.example.notification.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BidEvent {
    private UUID auctionId;
    private UUID userId;
    private double amount;
    private String eventType; // "SUBMITTED", "VALIDATED"
    private long timestamp;

    // getters and setters
}