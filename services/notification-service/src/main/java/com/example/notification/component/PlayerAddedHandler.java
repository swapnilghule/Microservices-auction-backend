package com.example.notification.component;

import com.example.notification.handler.AuctionWebSocketHandler;
import com.example.notification.handler.OnEventHandler;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerAddedHandler implements OnEventHandler {
    private final AuctionWebSocketHandler webSocketHandler;

    @Autowired
    public PlayerAddedHandler(AuctionWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void handle(JsonNode payload) {
        System.out.println("Handling PlayerAdded: " + payload);
        webSocketHandler.broadcast("Player update: " + payload.toString());
    }
}