package com.example.notification;

import com.example.notification.component.*;
import com.example.notification.handler.OnEventHandler;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class EventsConsumer {

  private final Map<String, OnEventHandler> handlers = new HashMap<>();
  private JsonUtils jsonUtils;

  @Autowired
  public void EventConsumer(AuctionStartedHandler auctionHandler,
                            BidPlacedHandler bidHandler,
                            WinnerDeclaredHandler winnerDeclaredHandler,
                            PlayerPoolCompleted playerPoolCompleted,
                            PlayerAddedHandler playerHandler,
                            UserRegisteredHandler userRegisteredHandler,
                            JsonUtils jsonUtils) {
    this.jsonUtils = jsonUtils;

    // Map eventType -> handler
    handlers.put("PlayerPoolCompleted", playerPoolCompleted);

    handlers.put("WinnerDeclared", winnerDeclaredHandler);
    handlers.put("PlayerUpdated", playerHandler);
    handlers.put("AuctionReady", auctionHandler);
    handlers.put("BidPlaced", bidHandler);
    handlers.put("PlayerAdded", playerHandler);
    handlers.put("EventAuctionUserRegistered", userRegisteredHandler);
  }

  public EventsConsumer(JsonUtils jsonUtils) {
    this.jsonUtils = jsonUtils;
  }

  @KafkaListener(topics = {
          "#{@environment.getProperty('app.topics.auctionEvents')}",
          "#{@environment.getProperty('app.topics.bidEvents')}",
          "#{@environment.getProperty('app.topics.playerEvents')}"
  }, groupId = "#{@environment.getProperty('spring.kafka.consumer.group-id')}")
  public void onEvent(ConsumerRecord<String, String> record) {
    try {
      String normalizedValue = jsonUtils.normalize(record.value());
      JsonNode node = jsonUtils.parse(normalizedValue);
      String eventType = node.has("eventType") ? node.get("eventType").asText() : "Unknown";

      log.info("Handling Event: {}", record);

      OnEventHandler handler = handlers.get(eventType);
      if (handler != null) {
        handler.handle(node);
      } else {
        log.info("No handler for event: {}, payload: {}", eventType, node);
      }

    } catch (Exception e) {
      log.error("Failed to process record: {}", record.value(), e);
    }
  }
}
