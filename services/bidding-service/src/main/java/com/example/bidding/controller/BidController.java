package com.example.bidding.controller;
import com.example.bidding.BiddingService;
import com.example.bidding.domain.Bid;
import com.example.bidding.dto.PlayerUpdatedEvent;
import com.example.bidding.dto.UserDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/bids")
public class BidController {
  private final BiddingService biddingService;
  public BidController(BiddingService biddingService)
  {

    this.biddingService=biddingService;
  }
  public static record PlaceBidRequest(UUID auctionId, UUID playerId, UUID userId, Integer amount, Integer userPurse)
  {

  }
  @PostMapping
  public ResponseEntity<?> placeBid(@RequestBody PlaceBidRequest request) throws JsonProcessingException {
    PlayerUpdatedEvent playerEvent = biddingService.getActivePlayerEvent(request.auctionId());
    if (playerEvent == null) {
      return ResponseEntity.badRequest().body("No active bidding round for this auction");
    }

    System.out.println("Event: "+ playerEvent);
    System.out.println("auctionID"+ request.auctionId+ "playerId: "+ request.playerId+ " userId:"+ request.userId+ "amount"+request.amount );

    String result = biddingService.placeBid(
            new Bid(UUID.randomUUID(), request.auctionId, request.playerId, request.userId, request.amount, Instant.now()),
            playerEvent);

    return ResponseEntity.ok(result);
  }

  // Example placeholder methods for fetching DTOs from other microservices
//  private Player fetchPlayerDTO(UUID playerId) {
//    // Implement via Kafka event cache, REST call, or other method
//    return new PlayerDTO(); // example basePrice
//  }
//
//  private UserDTO fetchUserDTO(UUID userId) {
//    // Implement via Kafka event cache, REST call, or other method
//    return new UserDTO(); // example purseAmount
//  }


}