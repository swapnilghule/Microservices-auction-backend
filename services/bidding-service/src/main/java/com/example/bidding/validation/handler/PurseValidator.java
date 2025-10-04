package com.example.bidding.validation.handler;

import com.example.bidding.domain.Bid;
import com.example.bidding.dto.PlayerUpdatedEvent;
import com.example.bidding.validation.BidValidator;
import org.springframework.stereotype.Component;

@Component
public class PurseValidator implements BidValidator {
    @Override
    public String validate(Bid bid, PlayerUpdatedEvent currentPlayer, Integer purse) {
        if (purse < bid.getAmount()) {
            return "Your bid exceeds the available balance in your purse.";
        }
        return null;
    }
}
