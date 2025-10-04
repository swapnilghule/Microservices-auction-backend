CREATE TABLE IF NOT EXISTS bids (
  id UUID PRIMARY KEY, auction_id UUID NOT NULL, player_id UUID NOT NULL, user_id UUID NOT NULL,
  amount NUMERIC(12,2) NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS outbox_bid_events (
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