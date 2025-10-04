package com.example.notification.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuctionRedisService {
    private final StringRedisTemplate redisTemplate;

    public AuctionRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Add registered user
    public void addRegisteredUser(UUID auctionId, UUID userId) {
        redisTemplate.opsForSet().add("auction:" + auctionId + ":registeredUsers", userId.toString());
    }

    // Check if user is registered
    public boolean isUserRegistered(UUID auctionId, UUID userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("auction:" + auctionId + ":registeredUsers", userId.toString()));
    }

    // Add joined user
    public void addJoinedUser(UUID auctionId, UUID userId) {
        redisTemplate.opsForSet().add("auction:" + auctionId + ":joinedUsers", userId.toString());
    }

    // Check if all registered users have joined
    public boolean areAllUsersJoined(UUID auctionId) {
        Long registered = redisTemplate.opsForSet().size("auction:" + auctionId + ":registeredUsers");
        Long joined = redisTemplate.opsForSet().size("auction:" + auctionId + ":joinedUsers");
        return registered != null && joined != null && registered.equals(joined);
    }
}
