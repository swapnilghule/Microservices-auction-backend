
package com.example.auction_service.api;

import com.example.auction_service.AuctionServiceApp;
import com.example.auction_service.domain.Auction;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController @RequestMapping("/auctions")
public class AuctionController {
  private final AuctionServiceApp app;
  public AuctionController(AuctionServiceApp app){ this.app = app; }

  public record CreateReq(String name){}
  public record BaseReq(UUID playerId, BigDecimal base) {}

  @PostMapping public ResponseEntity<?> create(@RequestBody CreateReq req) throws JsonProcessingException {
    Auction auction = app.create(req.name());
    return ResponseEntity.ok(Map.of("auctionId", auction));
  }
  @PutMapping("/{id}/start")
  public ResponseEntity<?> start(@PathVariable("id") UUID id) throws JsonProcessingException {
    Auction updatedAuction= app.start(id);
    return ResponseEntity.ok(Map.of("auctionId", updatedAuction));
  }

  @PutMapping("/{id}/end")
  public ResponseEntity<?> end(@PathVariable("id") UUID id) throws JsonProcessingException {
    Auction updatedAuction= app.close(id);
    return ResponseEntity.ok(Map.of("auctionId", updatedAuction));
  }

}
