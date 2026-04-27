docker exec kafka kafka-topics.sh \
  --create \
  --if-not-exists \
  --topic delete-data-video \
  --bootstrap-server kafka:9092 \
  --partitions 2 \
  --replication-factor 1

docker exec kafka kafka-topics.sh \
  --create \
  --if-not-exists \
  --topic create-user-in-db \
  --bootstrap-server kafka:9092 \
  --partitions 1 \
  --replication-factor 1

docker exec kafka kafka-topics.sh \
  --create \
  --if-not-exists \
  --topic email-request \
  --bootstrap-server kafka:9092 \
  --partitions 1 \
  --replication-factor 1