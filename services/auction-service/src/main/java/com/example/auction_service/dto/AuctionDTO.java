package com.example.auction_service.dto;

import com.example.auction_service.domain.Auction;
import com.example.auction_service.domain.AuctionStatus;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class AuctionDTO {
    private UUID id;
    private String name;
    private AuctionStatus status;
    public String eventType;

    private Instant createdAt;
    private List<UserDTO> registeredUsers = new ArrayList<>();


    public AuctionDTO() {}
    public AuctionDTO(Auction auction, String eventType, List<UserDTO> registeredUsers) {
        this.id = auction.getId();
        this.name = auction.getName();
        this.status = auction.getStatus();
        this.createdAt = auction.getCreatedAt();
        this.eventType = eventType;
        this.registeredUsers = registeredUsers;

    }
}
