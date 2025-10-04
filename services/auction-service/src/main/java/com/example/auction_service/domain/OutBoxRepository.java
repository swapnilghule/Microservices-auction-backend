package com.example.auction_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutBoxRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query(value = "SELECT * FROM outbox_auction_events WHERE published = false ORDER BY occurred_at LIMIT 100", nativeQuery = true)
    List<OutboxEvent> findBatchToPublish ();
}