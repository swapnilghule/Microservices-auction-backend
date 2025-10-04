package com.example.bidding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
// UserDTO placeholder — you need to replicate it in bidding service
public class UserDTO {
    private UUID userId;
    private String name;
    private Integer purseAmount;



}