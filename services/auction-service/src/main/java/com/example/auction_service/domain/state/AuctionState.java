
package com.example.auction_service.domain.state;

import com.example.auction_service.domain.Auction;
import com.example.auction_service.domain.OutBoxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

public interface AuctionState {

  void start(Auction auction) throws JsonProcessingException;

  String name();

  void close(Auction auction) throws JsonProcessingException;
}
