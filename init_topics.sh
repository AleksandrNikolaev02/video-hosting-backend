docker exec kafka kafka-topics.sh \
  --create \
  --if-not-exists \
  --topic delete-data-video \
  --bootstrap-server kafka:9092 \
  --partitions 2 \
  --replication-factor 1