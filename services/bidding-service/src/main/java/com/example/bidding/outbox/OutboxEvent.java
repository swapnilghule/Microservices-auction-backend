package com.example.bidding.outbox;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.Type;

import java.time.Instant; import java.util.UUID;
@Entity
@Table(name="outbox_events")


public class OutboxEvent {
    @Id
    private UUID id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String eventType;

    @Column(columnDefinition = "JSONB", nullable = false)
    @ColumnTransformer(write = "?::jsonb")
    private String payload;

    @Column(columnDefinition = "JSONB")
    @ColumnTransformer(write = "?::jsonb")
    private String headers;

    @Column(nullable = false)
    private Instant occurredAt = Instant.now();

    @Column(nullable = false)
    private boolean published = false;
  public OutboxEvent() {}
  public OutboxEvent(UUID id,String aggregateType,UUID aggregateId,String eventType,String headers,String payload,Instant occurredAt,boolean published)
  {
    this.id=id;
    this.aggregateType=aggregateType;
    this.aggregateId=aggregateId;
    this.eventType=eventType;
    this.payload=payload;
    this.headers= headers;
    this.occurredAt=occurredAt;
    this.published=published;
  }
  public boolean isPublished(){
    return published;
  }

  public void markPublished(){
    this.published=true;
  }
  public String getPayload(){
    return payload;
  }

  public String getEventType(){
    return eventType;
  }
}