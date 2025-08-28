
package com.example.auction_service.domain.state;


import com.example.auction_service.domain.Auction;
import com.example.auction_service.domain.AuctionStatus;
import com.example.auction_service.domain.OutBoxRepository;
import com.example.auction_service.exception.AuctionAlreadyStartedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

public class ClosedState implements AuctionState {


    @Override
    public void start(Auction auction) throws JsonProcessingException {
        if (auction.getStatus().equals(AuctionStatus.ENDED)) {
            System.out.println("throwing Exception, Auction Already Closed");
            throw new AuctionAlreadyStartedException("Auction with ID " + auction.getId() + " is already closed.");
        }
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public void close(Auction auction) throws JsonProcessingException {
        if (auction.getStatus().equals(AuctionStatus.STARTED)) {
            auction.setStatus(AuctionStatus.ENDED);
        } else {
            throw new AuctionAlreadyStartedException("Auction with ID " + auction.getId() + " is already closed.");
        }
    }
}
