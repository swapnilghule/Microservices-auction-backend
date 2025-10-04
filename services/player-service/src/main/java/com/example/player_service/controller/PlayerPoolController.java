package com.example.player_service.controller;

import com.example.player_service.entity.AuctionPool;
import com.example.player_service.services.PlayerPoolService;
import com.example.player_service.services.PlayerService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pool")
public class PlayerPoolController {

    private final PlayerPoolService service;

    public PlayerPoolController(PlayerPoolService service) {
        this.service = service;
    }

    @PostMapping("/{auctionId}/players/{playerId}")
    public UUID addPlayer(@PathVariable UUID auctionId, @PathVariable UUID playerId) throws Exception {
        return service.addPlayerToPool(auctionId, playerId);
    }

    @PostMapping("/{auctionId}/players")
    public List<UUID> addPlayers(
            @PathVariable UUID auctionId,
            @RequestBody List<UUID> playerIds) throws Exception {

        return service.addPlayersToPool(auctionId, playerIds);
    }

    @DeleteMapping("/{auctionId}/players/{playerId}")
    public void removePlayer(@PathVariable UUID auctionId, @PathVariable UUID playerId) throws Exception {
        service.removePlayerFromPool(auctionId, playerId);
    }

    @GetMapping("/{auctionId}/players")
    public List<AuctionPool> getPool(@PathVariable UUID auctionId) {
        return service.getPoolByAuctionId(auctionId);
    }
}
