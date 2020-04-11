# heart-beat-validator

> Service streaming heart beats from topic `heart-beats` to `heart-beats-valid` for valid heart beats and
> `heart-beats-invalid` for invalid ones.

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
java -jar target/heart-beat-validator.jar

# run with docker in host network
docker run -it --rm --name heart-beat-validator --net host \
    -p 8280:8280 \
    linlouis/heart-beat-validator

# run with docker in the services network
docker run -it --rm --name heart-beat-validator --net "${PWD##*/}_default" \
    -p 8280:8280 \
    linlouis/heart-beat-validator \
    --spring.kafka.bootstrap-servers=kafka:29092 \
    --spring.kafka.properties.schema.registry.url=http://schema-registry:8081

# observe the topic "heart-beats-valid"
docker exec -it schema-registry \
    /usr/bin/kafka-avro-console-consumer \
    --bootstrap-server kafka:29092 \
    --topic heart-beats-valid \
    --from-beginning
# observe the topic "heart-beats-invalid"
docker exec -it schema-registry \
    /usr/bin/kafka-avro-console-consumer \
    --bootstrap-server kafka:29092 \
    --topic heart-beats-invalid \
    --from-beginning
```
