//package com.example.notification;
//
//import com.example.notification.handler.AuctionWebSocketHandler;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AuctionEventsConsumer {
//    private final AuctionWebSocketHandler webSocketHandler;
//    public AuctionEventsConsumer(AuctionWebSocketHandler webSocketHandler) {
//        this.webSocketHandler = webSocketHandler;
//    }
//
//    @KafkaListener(
//            topics = "#{@environment.getProperty('app.topics.auctionEvents')}",
//            groupId = "notification-auction"
//    )
//    public void onAuctionEvent(ConsumerRecord<String, String> record) {
//        System.out.println("NotificationService in Auction received: key=" + record.key() + " value=" + record.value());
//
//        webSocketHandler.broadcast("Auction update: " + record.value());
//    }
//}