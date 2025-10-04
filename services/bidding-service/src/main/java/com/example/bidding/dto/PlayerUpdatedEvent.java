package com.example.bidding.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class PlayerUpdatedEvent {
    private String eventType;
    private UUID auctionId;
    private UUID playerId;
    private Instant updatedAt;
    private List<UserDTO> participants;

    public PlayerUpdatedEvent() {}

    public PlayerUpdatedEvent(String eventType, UUID auctionId, UUID playerId, Instant updatedAt, List<UserDTO> participants) {
        this.eventType = eventType;
        this.auctionId = auctionId;
        this.playerId = playerId;
        this.updatedAt = updatedAt;
        this.participants = participants;
    }

    // Convenience constructor for default eventType
    public PlayerUpdatedEvent(UUID auctionId, UUID playerId, Instant updatedAt, List<UserDTO> participants) {
        this("PlayerUpdated", auctionId, playerId, updatedAt, participants);
    }

    // Getters & setters
    public String getEventType() { return eventType; }
    public UUID getAuctionId() { return auctionId; }
    public UUID getPlayerId() { return playerId; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<UserDTO> getParticipants() { return participants; }

    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setAuctionId(UUID auctionId) { this.auctionId = auctionId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setParticipants(List<UserDTO> participants) { this.participants = participants; }
}