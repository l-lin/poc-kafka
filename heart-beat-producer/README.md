# heart-beat-producer

> Small webapp that exposes a REST endpoint to send new heart beats to Kafka.

## Getting started
### Build

```bash
# build maven project & build docker image
mvn clean package
```

### Run

Using [docker-compose-dep.yml](../docker-compose-dep.yml), you can launch this application for fast debugging
purpose as it is [already configured](src/main/resources/application.yml) to use the services.

```bash
# using java
java -jar target/heart-beat-producer.jar

# run with docker in host network
docker run -it --rm --name heart-beat-producer --net host \
    -p 8180:8180 \
    linlouis/heart-beat-producer

# run with docker in the services network
docker run -it --rm --name heart-beat-producer --net "${PWD##*/}_default" \
    -p 8180:8180 \
    linlouis/heart-beat-producer \
    --spring.kafka.bootstrap-servers=kafka:29092 \
    --spring.kafka.properties.schema.registry.url=http://schema-registry:8081

# observe the topic "heart-beats"
docker exec -it schema-registry \
    /usr/bin/kafka-avro-console-consumer \
    --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 \
    --topic heart-beats \
    --from-beginning
```

## Available endpoints

Using [HTTPie](https://httpie.org):

```bash
# send a single heart beat to kafka
http :8180/heart-beat-producer/heart-beats userId=1 hri=70 qrs=A
```

