package com.example.notification.handler;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class AuctionWebSocketHandler extends TextWebSocketHandler {
    private final StringRedisTemplate redisTemplate;


    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public AuctionWebSocketHandler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        String userId = (String) session.getAttributes().get("userId");
        String auctionId = (String) session.getAttributes().get("auctionId");

        redisTemplate.opsForSet()
                .remove("auction:" + auctionId + ":joinedUsers", userId);
        sessions.remove(session);
    }

    public void broadcast(String message) {
        sessions.forEach(session -> {
            synchronized (session) {
                try { session.sendMessage(new TextMessage(message)); }
                catch (IOException e) { e.printStackTrace(); }
            }
        });
    }
}