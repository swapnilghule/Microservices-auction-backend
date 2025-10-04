package com.example.player_service.controller;

import com.example.player_service.services.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @PostMapping
    public UUID addPlayer(@RequestParam String name, @RequestParam String role, @RequestParam double basePrice) throws Exception {
        return service.addPlayer(name, role, basePrice);
    }

    @PutMapping("/{playerId}")
    public void updatePlayer(@PathVariable UUID playerId,
                             @RequestParam String name,
                             @RequestParam String role,
                             @RequestParam double basePrice) throws Exception {
        service.updatePlayer(playerId, name, role, basePrice);
    }

    @DeleteMapping("/{playerId}")
    public void removePlayer(@PathVariable UUID playerId) throws Exception {
        service.removePlayer(playerId);
    }
}