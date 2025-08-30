package com.example.notification;
import com.example.notification.handler.AuctionWebSocketHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord; import org.springframework.kafka.annotation.KafkaListener; import org.springframework.stereotype.Component;
@Component
public class BidEventsConsumer {

  private final AuctionWebSocketHandler webSocketHandler;

  public BidEventsConsumer(AuctionWebSocketHandler webSocketHandler) {
    this.webSocketHandler = webSocketHandler;
  }

  @KafkaListener(topics="#{@environment.getProperty('app.topics.bidEvents')}", groupId="#{@environment.getProperty('spring.kafka.consumer.group-id')}")
  public void onMessage(ConsumerRecord<String,String> record){

    System.out.println("NotificationService received: key=" + record.key() + " value=" + record.value());
    webSocketHandler.broadcast("New bid: " + record.value());


  }

  @KafkaListener(topics="#{@environment.getProperty('app.topics.auctionEvents')}", groupId="#{@environment.getProperty('spring.kafka.consumer.group-id')}")
  public void onAuctionCreated(ConsumerRecord<String,String> record){

    System.out.println("NotificationService received: key=" + record.key() + " value=" + record.value());
    webSocketHandler.broadcast("Auction update: " + record.value());



  }
}