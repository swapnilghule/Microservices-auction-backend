package com.example.auction_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "winner_summary")
public class WinnerSummary {

    @Id
    private UUID bidId;

    private UUID auctionId;
    private UUID playerId;
    private UUID userId;
    private Integer amount;
    private Instant declaredAt;

    public WinnerSummary() {
    }

    public WinnerSummary(UUID bidId, UUID auctionId, UUID playerId, UUID userId,
                         Integer amount, Instant declaredAt) {
        this.bidId = bidId;
        this.auctionId = auctionId;
        this.playerId = playerId;
        this.userId = userId;
        this.amount = amount;
        this.declaredAt = declaredAt;
    }

    // getters and setters
    public UUID getBidId() { return bidId; }
    public void setBidId(UUID bidId) { this.bidId = bidId; }

    public UUID getAuctionId() { return auctionId; }
    public void setAuctionId(UUID auctionId) { this.auctionId = auctionId; }

    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public Instant getDeclaredAt() { return declaredAt; }
    public void setDeclaredAt(Instant declaredAt) { this.declaredAt = declaredAt; }
}
