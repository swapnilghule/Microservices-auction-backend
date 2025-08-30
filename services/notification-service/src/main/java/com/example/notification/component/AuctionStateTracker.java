package com.example.notification.component;

import com.example.notification.dto.BidEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuctionStateTracker {

    // Tracks current state of each auction
    private final Map<UUID, String> auctionStates = new ConcurrentHashMap<>();

    // Tracks current highest bid for each auction
    private final Map<UUID, BidEvent> highestBids = new ConcurrentHashMap<>();

    public void setAuctionState(UUID auctionId, String state) {
        auctionStates.put(auctionId, state);
    }

    public String getAuctionState(UUID auctionId) {
        return auctionStates.getOrDefault(auctionId, "NOT_STARTED");
    }

    public void updateHighestBid(UUID auctionId, BidEvent bid) {
        highestBids.merge(auctionId, bid, (oldBid, newBid) ->
                newBid.getAmount() > oldBid.getAmount() ? newBid : oldBid);
    }

    public BidEvent getHighestBid(UUID auctionId) {
        return highestBids.get(auctionId);
    }
}