package com.example.auction_service.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public class AuctionRegistrationDTO {
    @Getter
    private UUID id;
    @Getter
    private UUID auctionId;
    @Getter
    private UUID userId;
    public String eventType="EventAuctionUserRegistered";

    @Getter
    private Instant registeredAt;

    public AuctionRegistrationDTO() {}

    public AuctionRegistrationDTO(UUID id, UUID auctionId, UUID userId, Instant registeredAt) {
        this.id = id;
        this.auctionId = auctionId;
        this.userId = userId;
        this.registeredAt = registeredAt;
    }

}
