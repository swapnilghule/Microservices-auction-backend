package com.example.notification.config;

import com.example.notification.interceptor.AuctionHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.notification.handler.AuctionWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private AuctionWebSocketHandler auctionWebSocketHandler;

    @Autowired
    private AuctionHandshakeInterceptor handshakeInterceptor;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(auctionWebSocketHandler, "/ws/auction")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*"); // allow all origins (or restrict for security)
    }
}