package com.example.player_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {
    private UUID userId;            // make sure this name matches JSON key (see note below)
    private String name;
    private Integer purseAmount;
}
