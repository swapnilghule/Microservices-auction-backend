
package com.example.auction_service.domain.state;

import com.example.auction_service.domain.Auction;
import com.example.auction_service.domain.AuctionStatus;
import com.example.auction_service.domain.OutBoxRepository;
import com.example.auction_service.domain.OutboxEvent;
import com.example.auction_service.domain.validation.CompositeBidValidator;
import com.example.auction_service.exception.AuctionAlreadyStartedException;
import com.example.common.events.AuctionLifecycle;
import com.example.common.events.BidPlacedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class LiveState implements AuctionState {

    public LiveState() {
    }
    @Override
    public void start(Auction auction)  {
        if (auction.getStatus().equals(AuctionStatus.STARTED)) {
            System.out.println("throwing Exception, Auction Already Started");
            throw new AuctionAlreadyStartedException("Auction with ID " + auction.getId() + " is already live.");
        }
    }


    public String name(){ return "LIVE"; }

    @Override
    public void close(Auction auction) throws JsonProcessingException {
        auction.setStatus(AuctionStatus.ENDED);
    }

}
