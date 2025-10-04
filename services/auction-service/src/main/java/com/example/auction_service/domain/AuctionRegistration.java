package com.example.auction_service.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "auction_registrations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"auction_id", "user_id"}))
public class AuctionRegistration {

    @Getter
    @Id
    private UUID id;

    @Getter
    @Column(name = "auction_id", nullable = false)
    private UUID auctionId;

    @Getter
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_purse", nullable = false)
    private Integer userPurse = 8000;

    protected AuctionRegistration() {}

    public AuctionRegistration(UUID id, UUID auctionId, UUID userId) {
        this.id = id;
        this.auctionId = auctionId;
        this.userId = userId;
        this.userPurse = 8000; // default value
    }


}
