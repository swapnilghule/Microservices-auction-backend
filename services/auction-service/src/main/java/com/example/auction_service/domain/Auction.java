package com.example.auction_service.domain;

import com.example.auction_service.domain.state.*;
import com.example.common.events.AuctionLifecycle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name= "auctions")
public class Auction {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    public Auction(){

    }
    public Auction(UUID id, AuctionStatus status, String name, Instant createdAt) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.createdAt = createdAt;
    }

    @Transient
    @JsonIgnore
    private AuctionState state;

    private String name;
    @Column(name = "created_at")
    private Instant createdAt;

    @JsonIgnore
    public AuctionState getStateHandler() {
        return switch (status) {
            case CREATED -> new CreatedState();
            case STARTED -> new LiveState();
            case ENDED -> new ClosedState();
        };
    }

//    public void start(OutBoxRepository outboxRepository, ObjectMapper mapper) throws JsonProcessingException {
//        state.start(this, outboxRepository, mapper);
//    }
//
//    public void close(OutBoxRepository outboxRepository) throws JsonProcessingException {
//        state.close(this, outboxRepository);
//    }

}
