package com.example.bidding.validation.handler;

import com.example.bidding.domain.Bid;
import com.example.bidding.dto.PlayerUpdatedEvent;
import com.example.bidding.validation.BidValidator;
import org.springframework.stereotype.Component;

@Component
public class BasePriceValidator implements BidValidator {
    @Override
    public String validate(Bid bid, PlayerUpdatedEvent currentPlayer, Integer purse) {
        System.out.println(currentPlayer.getAuctionId());
        if (bid.getAmount() <bid.getAmount()) {
            return "The amount must be greater than the base price.";
        }
        return null;
    }
}