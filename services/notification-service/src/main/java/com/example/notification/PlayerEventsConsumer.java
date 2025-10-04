//package com.example.notification;
//
//import com.example.notification.handler.AuctionWebSocketHandler;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PlayerEventsConsumer {
//    private final AuctionWebSocketHandler webSocketHandler;
//    public PlayerEventsConsumer(AuctionWebSocketHandler webSocketHandler) {
//        this.webSocketHandler = webSocketHandler;
//    }
//
//    @KafkaListener(
//            topics = "#{@environment.getProperty('app.topics.playerEvents')}",
//            groupId = "notification-player"
//    )
//    public void onPlayerEvent(ConsumerRecord<String, String> record) {
//        System.out.println("NotificationService in Player received: key=" + record.key() + " value=" + record.value());
//
//        webSocketHandler.broadcast("Player update: " + record.value());
//    }
//}