package com.example.common.events;

import java.time.Instant;
import java.util.UUID;

public class AuctionLifecycle {

    public enum Type {CREATED, STARTED, CLOSED;
    };
    public UUID auctionId;
    public Type type;
    public Instant at;

    public AuctionLifecycle(UUID auctionId, Type type, Instant at) {
        this.auctionId = auctionId;
        this.type= type;
        this.at=at;
    }

    public AuctionLifecycle() {
    }
}
