package com.example.notification.component;

import com.example.notification.handler.AuctionWebSocketHandler;
import com.example.notification.handler.OnEventHandler;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerPoolCompleted  implements OnEventHandler {
    private final AuctionWebSocketHandler webSocketHandler;

    public PlayerPoolCompleted(AuctionWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }


    @Override
    public void handle(JsonNode payload) {
        System.out.println("Handling Pool Completed: " + payload);
        webSocketHandler.broadcast("Auction Completed: " + payload.toString());
    }
}
