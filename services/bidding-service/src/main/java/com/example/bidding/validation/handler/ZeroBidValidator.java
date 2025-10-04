package com.example.bidding.validation.handler;

import com.example.bidding.domain.Bid;
import com.example.bidding.dto.PlayerUpdatedEvent;
import com.example.bidding.validation.BidValidator;
import org.springframework.stereotype.Component;

@Component
public class ZeroBidValidator implements BidValidator {
    @Override
    public String validate(Bid bid, PlayerUpdatedEvent currentPlayer, Integer purse) {
        if (bid.getAmount() == 0) {
            return "The player has been passed. Please wait for other participants to submit their bids.";
        }
        return null;
    }
}
