//
//package com.example.auction_service.infra;
//
//import com.example.auction_service.AuctionServiceApp;
//import com.example.common.events.BidPlacedEvent;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class KafkaConsumers {
//  private final AuctionServiceApp app;
//  public KafkaConsumers(AuctionServiceApp app){ this.app = app; }
//
//  @KafkaListener(topics = "#{@environment.getProperty('app.topics.bids')}", groupId = "auction-service")
//  public void onBid(BidPlacedEvent e){
//    // If you want Auction to be source of truth, you would generate BidPlaced from Auction itself.
//    // Here we simply log what we received.
//    System.out.println("[auction-service] observed BidPlaced from bidding-service: " + e.playerId + " amount=" + e.amount);
//  }
//}
