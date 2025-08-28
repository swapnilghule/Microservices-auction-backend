package com.example.bidding.domain;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="bids")
public class Bid {
  @Id private UUID id;
  private UUID auctionId;
  private UUID playerId;
  private UUID userId;
  private BigDecimal amount;
  private Instant createdAt;
  public Bid() {}
  public Bid(UUID id, UUID auctionId, UUID playerId, UUID userId, BigDecimal amount, Instant createdAt){
    this.id=id;
    this.auctionId=auctionId;
    this.playerId=playerId;
    this.userId=userId;
    this.amount=amount;
    this.createdAt=createdAt;
  }
}