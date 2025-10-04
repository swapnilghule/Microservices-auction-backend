package com.example.bidding.validation.handler;

import com.example.bidding.domain.Bid;
import com.example.bidding.dto.PlayerUpdatedEvent;
import com.example.bidding.validation.BidValidator;
import org.springframework.stereotype.Component;

@Component
public class ActivePlayerValidator implements BidValidator {
    @Override
    public String validate(Bid bid, PlayerUpdatedEvent currentPlayer, Integer purse) {
        if (currentPlayer == null) {
            return "No players found. No active auction or players.";
        }
        return null;
    }
}