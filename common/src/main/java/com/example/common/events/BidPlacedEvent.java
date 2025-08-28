package com.example.common.events;
import java.math.BigDecimal;
import java.time.Instant; import java.util.UUID;
public class BidPlacedEvent {
  public String eventType="BidPlaced";
  public UUID bidId,auctionId,playerId,userId;
  public double amount;
  public Instant occurredAt;
  public BidPlacedEvent(){}

  public BidPlacedEvent(UUID bidId,UUID auctionId,UUID playerId,UUID userId,double amount,Instant occurredAt){
    this.bidId=bidId;
    this.auctionId=auctionId;
    this.playerId=playerId;
    this.userId=userId;
    this.amount=amount;
    this.occurredAt=occurredAt;
  }

  public BidPlacedEvent(UUID id, UUID playerId, UUID userId, BigDecimal amount, Instant now) {
  }
}


