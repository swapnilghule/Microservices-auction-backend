package com.example.auction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private List<UserDTO> participants;



}

