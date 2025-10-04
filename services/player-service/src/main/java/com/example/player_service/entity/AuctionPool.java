package com.example.player_service.entity;


import com.example.player_service.enums.PlayerStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "auction_pool")
public class AuctionPool {
    @Id
    private UUID id;           // unique row id
    private UUID auctionId;
    private UUID playerId;
    @Setter
    private PlayerStatus status; // AVAILABLE, SOLD, UNSOLD

    protected AuctionPool() {}

    private AuctionPool(Builder builder) {
        this.id = builder.id;
        this.auctionId = builder.auctionId;
        this.playerId = builder.playerId;
        this.status = builder.status;
    }

    public static class Builder {
        private UUID id = UUID.randomUUID();
        private UUID auctionId;
        private UUID playerId;
        private PlayerStatus status = PlayerStatus.AVAILABLE;

        public Builder auctionId(UUID auctionId) { this.auctionId = auctionId; return this; }
        public Builder playerId(UUID playerId) { this.playerId = playerId; return this; }
        public Builder status(PlayerStatus status) { this.status = status; return this; }
        public AuctionPool build() { return new AuctionPool(this); }
    }

    public UUID getId() { return id; }
    public UUID getAuctionId() { return auctionId; }
    public UUID getPlayerId() { return playerId; }
    public PlayerStatus getStatus() { return status; }
}
