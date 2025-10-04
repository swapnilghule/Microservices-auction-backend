package com.example.auction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDTO {
    private UUID userId;
    private Integer purseAmount;  // e.g., default 8000
    private String username;      // optional, helpful for display
}
