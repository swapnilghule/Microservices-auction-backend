
package com.example.auction_service.domain.state;

import com.example.auction_service.domain.Auction;
import com.example.auction_service.domain.AuctionStatus;
import com.example.auction_service.domain.OutBoxRepository;
import com.example.auction_service.domain.OutboxEvent;
import com.example.auction_service.exception.AuctionAlreadyStartedException;
import com.example.auction_service.exception.AuctionIsNotStartedExcpetion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class CreatedState implements AuctionState {
    public CreatedState() {

    }

  @Override
  public void start(Auction auction) {
        auction.setStatus(AuctionStatus.STARTED);
  }

  public String name(){ return "CREATED"; }

    @Override
    public void close(Auction auction) throws JsonProcessingException {
        System.out.println("throwing Exception, Auction not Started to make it live");
        throw new AuctionIsNotStartedExcpetion("Auction with ID " + auction.getId() + " is not live to close.");
    }

}
