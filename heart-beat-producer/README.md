# heart-beat-producer

Exposes endpoint to send new heart beats to Kafka.

## Getting started

```bash
# build maven project & build docker image
mvn clean package

# run in docker
# zookeeper, kafka & schema registry must be up
docker run -it --rm --name heart-beat-producer --net kafka-streams_default \
    -p 18080:8080 \
    linlouis/heart-beat-producer \
    --spring.kafka.bootstrap-servers=kafka1:9092,kafka2:9092,kafka3:9092 \
    --spring.kafka.properties.schema.registry.url=http://schema-registry:8081 \
    --topic.partitions=1 \
    --topic.replicas=3
```

## Available endpoints

```bash
# send a single heart beat to kafka
http :18080/heart-beats userId=1 hri=70 qrs=A
```
