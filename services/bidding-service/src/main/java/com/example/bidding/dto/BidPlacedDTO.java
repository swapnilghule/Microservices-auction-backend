package com.example.bidding.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidPlacedDTO {
    private UUID auctionId;
    private UUID playerId;
    private UUID userId;
    private Double amount;
}
