//
//package com.example.auction_service.infra;
//
//import com.example.common.events.AuctionLifecycle;
//import com.example.common.events.BidPlacedEvent;
//import com.example.common.events.BidPlacedEvent;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.event.EventListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class KafkaEventPublisher {
//  private final KafkaTemplate<String, Object> kafka;
//  private final String auctionsTopic;
//  private final String bidsTopic;
//  public KafkaEventPublisher(KafkaTemplate<String,Object> kafka,
//                             @Value("${app.topics.auctions}") String auctionsTopic,
//                             @Value("${app.topics.bids}") String bidsTopic){
//    this.kafka = kafka; this.auctionsTopic = auctionsTopic; this.bidsTopic = bidsTopic;
//  }
//
//  @EventListener
//  public void onLifecycle(AuctionLifecycle e){
//    kafka.send(auctionsTopic, e.auctionId.toString(), e);
//  }
//
//  @EventListener
//  public void onBid(BidPlacedEvent e){
//    kafka.send(bidsTopic, e.playerId.toString(), e);
//  }
//}
