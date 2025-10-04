package com.example.player_service.outbox;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(value="SELECT * FROM outbox_players_events WHERE published = false ORDER BY occurred_at LIMIT 100", nativeQuery=true)
    List<OutboxEvent> findBatchToPublish();
}
