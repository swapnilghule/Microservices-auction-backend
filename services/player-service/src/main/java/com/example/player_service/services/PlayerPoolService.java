package com.example.player_service.services;

import com.example.player_service.enums.PlayerStatus;
import com.example.player_service.events.PlayerUpdatedEvent;
import com.example.player_service.events.UserDetails;
import com.example.player_service.outbox.OutboxEvent;
import com.example.player_service.entity.AuctionPool;
import com.example.player_service.events.pool.PlayerAddedToPoolEvent;
import com.example.player_service.repository.AuctionPoolRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.player_service.outbox.OutboxRepository;
import com.example.player_service.events.pool.*;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PlayerPoolService {

    private final AuctionPoolRepository poolRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final String topic;
    private static final Logger log = Logger.getLogger(PlayerPoolService.class.getName());

    public PlayerPoolService(AuctionPoolRepository poolRepository,
                             OutboxRepository outboxRepository,
                             ObjectMapper mapper,
                             KafkaTemplate<String,String> kafkaTemplate,
                             Environment env) {
        this.poolRepository = poolRepository;
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = env.getProperty("app.topics.playerEvents");
    }

    private final Map<UUID, Queue<UUID>> auctionPlayerQueues = new HashMap<>();

    // Initialize queue when AuctionReady occurs
    public void getPoolByAuction(UUID auctionId, List<UserDetails> registeredUser) throws JsonProcessingException {
        log.info("Inside get Pool: " + registeredUser);

        if (!auctionPlayerQueues.containsKey(auctionId)) {
            log.info("Inside get queue check: " + auctionId);

            List<AuctionPool> pool = fetchPlayersFromDb(auctionId);
            Queue<UUID> queue = pool.stream()
                    .map(AuctionPool::getPlayerId)
                    .collect(Collectors.toCollection(LinkedList::new));
            auctionPlayerQueues.put(auctionId, queue);
        }

        log.info("Display next player calling: " + auctionId);

        // Trigger first player display
        displayNextPlayer(auctionId, registeredUser);
    }

    // Pop and emit next player
    public void displayNextPlayer(UUID auctionId, List<UserDetails> registeredUser) throws JsonProcessingException {
        log.info("Inside Display next player calling: " + auctionId);

        Queue<UUID> queue = auctionPlayerQueues.get(auctionId);
        if (queue != null && !queue.isEmpty()) {
            log.info("Inside queue check next player calling: " + auctionId);

            UUID playerId = queue.peek(); // peek first player
            boolean valid = validatePlayer(playerId);
            if (valid) {
                log.info("Inside valid check next PublishEvent calling: " + auctionId);

                // Publish event for frontend/display
                publishPlayerDisplayEvent(auctionId, playerId, registeredUser);
                queue.poll();
            }
        } else {
            PlayerUpdatedEvent event = new PlayerUpdatedEvent("PlayerPoolCompleted", auctionId, null, Instant.now(), registeredUser);
            kafkaTemplate.send("player-events.v1", "PlayerPoolCompleted", mapper.writeValueAsString(event));
        }
    }

    private boolean validatePlayer(UUID playerId) {
        // Your validation logic: e.g., purse, category limit
        return true;
    }

    private void publishPlayerDisplayEvent(UUID auctionId, UUID playerId, List<UserDetails> participants) throws JsonProcessingException {
        // Use KafkaTemplate to emit event to "player-display-events.v1"
        PlayerUpdatedEvent event = new PlayerUpdatedEvent("PlayerUpdated",auctionId, playerId, Instant.now(), participants);
        log.info("Event: " + event);
        kafkaTemplate.send("player-events.v1", "PlayerUpdated", mapper.writeValueAsString(event));
    }

    private List<AuctionPool> fetchPlayersFromDb(UUID auctionId) {
        // Query DB for players in auction pool
        return poolRepository.findByAuctionId(auctionId);
    }

    @Transactional
    public UUID addPlayerToPool(UUID auctionId, UUID playerId) throws Exception {
        AuctionPool poolEntry = new AuctionPool.Builder()
                .auctionId(auctionId)
                .playerId(playerId)
                .build();

        poolRepository.save(poolEntry);

        String payload = mapper.writeValueAsString(new PlayerAddedToPoolEvent(auctionId, playerId, Instant.now()));
        outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "PLAYER_POOL", playerId, "PlayerAddedToPool","{}", payload, Instant.now(), false));
        return poolEntry.getId();
    }

    public List<UUID> addPlayersToPool(UUID auctionId, List<UUID> playerIds) {
        List<UUID> savedIds = new ArrayList<>();
        for (UUID playerId : playerIds) {
            AuctionPool pool = new AuctionPool.Builder()
                    .auctionId(auctionId)
                    .playerId(playerId)
                    .status(PlayerStatus.AVAILABLE)
                    .build();
            poolRepository.save(pool);
            savedIds.add(pool.getId());
        }
        return savedIds;
    }

    @Transactional
    public void removePlayerFromPool(UUID auctionId, UUID playerId) throws Exception {
        List<AuctionPool> entries = poolRepository.findByAuctionId(auctionId).stream()
                .filter(e -> e.getPlayerId().equals(playerId))
                .toList();
        poolRepository.deleteAll(entries);

        String payload = mapper.writeValueAsString(new PlayerRemovedFromPoolEvent(auctionId, playerId, Instant.now()));
        outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "PLAYER_POOL", playerId, "PlayerRemovedFromPool","{}", payload, Instant.now(), false));
    }

    public List<AuctionPool> getPoolByAuctionId(UUID auctionId) {
        return poolRepository.findByAuctionId(auctionId);
    }

    @Scheduled(fixedDelay = 1000)
    public void publishOutboxBatch() {
        List<OutboxEvent> events = outboxRepository.findBatchToPublish();
        for (OutboxEvent e : events) {
            try {
                kafkaTemplate.send(topic, e.getEventType(), e.getPayload()).get();
                e.markPublished();
            } catch (Exception ex) {
                log.severe("Failed to publish outbox event: " + ex.getMessage());
            }
        }
        outboxRepository.saveAll(events.stream().filter(OutboxEvent::isPublished).toList());
    }
}
