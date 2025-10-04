package com.example.player_service.repository;

import com.example.player_service.entity.AuctionPool;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AuctionPoolRepository extends JpaRepository<AuctionPool, UUID> {
    List<AuctionPool> findByAuctionId(UUID auctionId);
}