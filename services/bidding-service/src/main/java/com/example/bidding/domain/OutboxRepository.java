package com.example.bidding.domain;

import com.example.bidding.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
  @Query(value="SELECT * FROM outbox_bid_events WHERE published = false ORDER BY occurred_at LIMIT 100", nativeQuery=true)
  List<OutboxEvent> findBatchToPublish();
}
