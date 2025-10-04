package com.example.bidding.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BiddingRound {

    @Getter
    private final UUID playerId;      // The player being bid on
    @Getter
    private final List<Bid> bids;    // All bids placed for this round
    private boolean closed = false;
    private final UUID auctionId;
// Whether the round is finished

    public BiddingRound(UUID playerId, UUID auctionId) {
        this.playerId = playerId;
        this.bids = new ArrayList<>();
        this.auctionId = auctionId;

    }

    public void addBid(Bid bid) {
        if (!closed) {
            System.out.println("Added Bid"+ bid);
            bids.add(bid);
            System.out.println("Added to"+ bids);

        } else {
            throw new IllegalStateException("Bidding round already closed for player " + playerId);
        }
    }

    public void closeRound() {
        this.closed = true;
    }

    public void close() {
    }
}
