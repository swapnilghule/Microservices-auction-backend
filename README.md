# Auction Backend - Tracer Bullet (Kafka + Outbox)
## Run infra
docker compose up -d
# Kafka UI: http://localhost:8080
./topics.sh
## Build & run
mvn -q -DskipTests clean install
mvn -q -pl services/bidding-service -am spring-boot:run
# in another terminal
mvn -q -pl services/notification-service -am spring-boot:run
## Test
curl -X POST http://localhost:8081/bids -H 'Content-Type: application/json' -d '{
  "auctionId":"11111111-1111-1111-1111-111111111111",
  "playerId":"22222222-2222-2222-2222-222222222222",
  "userId":"33333333-3333-3333-3333-333333333333",
  "amount":1500.00
}'
