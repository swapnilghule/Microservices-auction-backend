package com.example.player_service.services;
import com.example.player_service.entity.Player;
import com.example.player_service.events.PlayerAddedEvent;
import com.example.player_service.events.PlayerRemovedEvent;
import com.example.player_service.events.PlayerUpdatedEvent;
import com.example.player_service.outbox.OutboxEvent;
import com.example.player_service.outbox.OutboxRepository;
import com.example.player_service.repository.PlayerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private static final Logger log = Logger.getLogger(PlayerService.class.getName());

    public PlayerService(PlayerRepository playerRepository,
                         OutboxRepository outboxRepository,
                         ObjectMapper mapper,
                         KafkaTemplate<String, String> kafkaTemplate,
                         Environment env) {
        this.playerRepository = playerRepository;
        this.outboxRepository = outboxRepository;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = env.getProperty("app.topics.playerEvents");
    }

    @Transactional
    public UUID addPlayer(String name, String role, double basePrice) throws Exception {
        UUID playerId = UUID.randomUUID();
        Player player = new Player.Builder()
                .playerId(playerId)
                .name(name)
                .role(role)
                .basePrice(basePrice)
                .build();

        playerRepository.save(player);

        String payload = mapper.writeValueAsString(new PlayerAddedEvent(playerId, name, role, basePrice, Instant.now()));
        outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "PLAYER", playerId, "PlayerAdded", payload, "{}", Instant.now(), false));

        return playerId;
    }

    @Transactional
    public void updatePlayer(UUID playerId, String name, String role, double basePrice) throws Exception {
        Player player = playerRepository.findById(playerId).orElseThrow();
        player = new Player.Builder()
                .playerId(player.getPlayerId())
                .name(name)
                .role(role)
                .basePrice(basePrice)
                .status(player.getStatus())
                .build();

        playerRepository.save(player);

     //   String payload = mapper.writeValueAsString(new PlayerUpdatedEvent(playerId, name, role, basePrice, Instant.now()));
      //  outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "PLAYER", playerId, "PlayerUpdated", "{}", payload, Instant.now(), false));
    }

    @Transactional
    public void removePlayer(UUID playerId) throws Exception {
        playerRepository.deleteById(playerId);
        String payload = mapper.writeValueAsString(new PlayerRemovedEvent(playerId, Instant.now()));
        outboxRepository.save(new OutboxEvent(UUID.randomUUID(), "PLAYER", playerId, "PlayerRemoved", "{}", payload, Instant.now(), false));
    }

    @Scheduled(fixedDelay = 1000)
    public void publishOutboxBatch() {
        List<OutboxEvent> events = outboxRepository.findBatchToPublish();
        for (OutboxEvent e : events) {
            try {
                System.out.println("Publishing event: topic=" + topic + ", eventType=" + e.getEventType() + ", payload=" + e.getPayload());
                kafkaTemplate.send(topic, e.getEventType(), e.getPayload()).get();
                e.markPublished();
            } catch (Exception ex) {
                log.severe("Failed to publish outbox event: " + ex.getMessage());
            }
        }
        outboxRepository.saveAll(events.stream().filter(OutboxEvent::isPublished).toList());
    }
}
