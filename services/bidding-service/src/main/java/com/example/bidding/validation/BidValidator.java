package com.example.bidding.validation;

import com.example.bidding.domain.Bid;
import com.example.bidding.dto.PlayerUpdatedEvent;

public interface BidValidator {
    String validate(Bid bid, PlayerUpdatedEvent currentPlayer, Integer purse);
}