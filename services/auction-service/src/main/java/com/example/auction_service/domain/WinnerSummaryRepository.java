package com.example.auction_service.domain;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WinnerSummaryRepository extends JpaRepository<WinnerSummary, UUID> {
}
