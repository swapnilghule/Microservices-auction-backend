#!/usr/bin/env bash
set -euo pipefail
BOOTSTRAP=${1:-localhost:9092}
kafka-topics --bootstrap-server $BOOTSTRAP --create --if-not-exists --topic bid-events.v1 --partitions 3 --replication-factor 1
echo "Topics created on $BOOTSTRAP"
