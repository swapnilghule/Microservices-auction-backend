package com.example.notification.component;

import com.example.notification.handler.AuctionWebSocketHandler;
import com.example.notification.handler.OnEventHandler;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class WinnerDeclaredHandler implements OnEventHandler {

    private final AuctionWebSocketHandler webSocketHandler;

    public WinnerDeclaredHandler(AuctionWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void handle(JsonNode payload) {
        System.out.println("Handling Winner Declared: " + payload);
        webSocketHandler.broadcast("Winner is: " + payload.toString());
    }
}
