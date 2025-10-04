package com.example.notification.interceptor;

import com.example.common.events.component.JwtUtil;
import com.example.notification.dto.AuctionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.HandshakeFailureException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.server.ServletServerHttpRequest;


import java.util.List;
import java.util.UUID;

@Component
public class AuctionHandshakeInterceptor implements HandshakeInterceptor {

    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public AuctionHandshakeInterceptor(StringRedisTemplate redisTemplate,
                                       KafkaTemplate<String, String> kafkaTemplate,
                                       ObjectMapper mapper) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }



    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String userId = null;
        String auctionId = null;

        System.out.println("Inside Interceptor: ");


        if (request instanceof ServletServerHttpRequest servletRequest) {
            System.out.println("Understandoing Request Interceptor: ");


            // Get the underlying HttpServletRequest
            var httpRequest = servletRequest.getServletRequest();
            userId = httpRequest.getParameter("userId");
            auctionId = httpRequest.getParameter("auctionId");
        }

        if (userId == null || auctionId == null) {
            System.out.println("Bad Request Interceptor: ");

            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false; // reject handshake
        }

        // Redis check example
        String redisKey = "auction:" + auctionId + ":registeredUsers";
        Boolean registered = redisTemplate.opsForSet().isMember(redisKey, userId);

        System.out.println("RedisKey: "+ redisKey);
        System.out.println("Registered: "+ registered);


        if (registered == null || !registered) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false; // reject handshake
        }
        else{
            String joinedKey = "auction:" + auctionId + ":joinedUsers";
            redisTemplate.opsForSet().add(joinedKey, userId);

            // Check if all users have joined
            Long registeredCount = redisTemplate.opsForSet().size(redisKey);
            Long joinedCount = redisTemplate.opsForSet().size(joinedKey);

            System.out.println("Comapring Registered count "+ registeredCount);

            System.out.println("with Joined = count "+ joinedCount);

            if (registeredCount != null && joinedCount != null && registeredCount.equals(joinedCount)) {
                // All users joined → publish AuctionReadyEvent
                AuctionEvent event = new AuctionEvent(auctionId, Instant.now());
                kafkaTemplate.send("auction-events.v1", "AuctionStarted", mapper.writeValueAsString(event));
            }
        }

        // Save info in session attributes
        attributes.put("userId", userId);
        attributes.put("auctionId", auctionId);


        return true; // allow handshake
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}

