CREATE TYPE auction_state AS ENUM ('STARTED', 'CREATED', 'CLOSED');


CREATE TABLE IF NOT EXISTS auctions (
    id UUID PRIMARY KEY,
    state auction_state NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS outbox_events (
  id UUID PRIMARY KEY,
  aggregate_type TEXT NOT NULL,
  aggregate_id UUID NOT NULL,
  event_type TEXT NOT NULL,
  payload JSONB NOT NULL,
  headers JSONB USING headers::jsonb,
  occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  published BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_outbox_published ON outbox_events(published, occurred_at);