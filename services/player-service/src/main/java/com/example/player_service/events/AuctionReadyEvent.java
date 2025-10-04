package com.example.player_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionReadyEvent {
    private UUID id;
    private String name;
    private AuctionStatus status;
    private String eventType;
    private Instant createdAt;

    // initialize to avoid null
    private List<UserDetails> registeredUsers;
}
