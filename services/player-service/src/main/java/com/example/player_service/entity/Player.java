package com.example.player_service.entity;

import com.example.player_service.enums.PlayerStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "players")
public class Player {

    @Id
    private  UUID playerId;
    private  String name;
    private  String role; // Batsman, Bowler, All-rounder, WicketKeeper
    private  double basePrice;
    // Allow only status change via setter
    @Setter
    private PlayerStatus status; // AVAILABLE, SOLD, UNSOLD

    protected Player() {}


    private Player(Builder builder) {
        this.playerId = builder.playerId;
        this.name = builder.name;
        this.role = builder.role;
        this.basePrice = builder.basePrice;
        this.status = builder.status;
    }

    public static class Builder {
        private UUID playerId;
        private String name;
        private String role;
        private double basePrice;
        private PlayerStatus status = PlayerStatus.AVAILABLE;

        public Builder playerId(UUID playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder basePrice(double basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder status(PlayerStatus status) {
            this.status = status;
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }

    public UUID getPlayerId() { return playerId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public double getBasePrice() { return basePrice; }
    public PlayerStatus getStatus() { return status; }

}
