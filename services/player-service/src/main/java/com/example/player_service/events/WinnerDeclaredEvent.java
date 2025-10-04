package com.example.player_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WinnerDeclaredEvent {


    private UUID bidId;
    private UUID auctionId;
    private UUID playerId;
    private UUID userId;
    private Integer amount;
    private Instant declaredAt;
    private String eventType;
    private List<UserDetails> participants;



}

