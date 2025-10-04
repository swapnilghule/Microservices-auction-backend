package com.example.bidding.domain;
import com.example.bidding.dto.BidPlacedDTO;
import com.example.bidding.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
@Repository

public interface BidRepository extends JpaRepository<Bid, UUID> {

}
