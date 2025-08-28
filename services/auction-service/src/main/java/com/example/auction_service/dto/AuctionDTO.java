package com.example.auction_service.dto;

import com.example.auction_service.domain.Auction;
import com.example.auction_service.domain.AuctionStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AuctionDTO {
    private UUID id;
    private String name;
    private AuctionStatus status;
    private Instant createdAt;

    public AuctionDTO(Auction auction) {
        this.id = auction.getId();
        this.name = auction.getName();
        this.status = auction.getStatus();
        this.createdAt = auction.getCreatedAt();
    }
}
