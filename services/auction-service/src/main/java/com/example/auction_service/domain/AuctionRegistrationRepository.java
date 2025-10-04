package com.example.auction_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuctionRegistrationRepository extends JpaRepository<AuctionRegistration, UUID> {
    Optional<AuctionRegistration> findByAuctionIdAndUserId(UUID auctionId, UUID userId);

    List<AuctionRegistration> findByAuctionId(UUID id);
}
