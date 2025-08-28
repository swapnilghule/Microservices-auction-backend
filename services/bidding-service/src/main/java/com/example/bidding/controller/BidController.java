package com.example.bidding.controller;
import com.example.bidding.BiddingService; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal; import java.util.Map; import java.util.UUID;


@RestController @RequestMapping("/bids")
public class BidController {
  private final BiddingService biddingService;
  public BidController(BiddingService biddingService)
  {
    this.biddingService=biddingService;
  }
  public static record PlaceBidRequest(UUID auctionId, UUID playerId, UUID userId, BigDecimal amount)
  {

  }
  @PostMapping
  public ResponseEntity<?> place(@RequestBody PlaceBidRequest req) throws Exception {
    UUID bidId = biddingService.placeBid(req.auctionId(), req.playerId(), req.userId(), req.amount());
    return ResponseEntity.accepted().body(Map.of("bidId", bidId.toString(), "status", "accepted"));
  }
}