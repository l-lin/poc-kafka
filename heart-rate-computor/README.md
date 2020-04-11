# heart-rate-computor

> Service streaming valid heart beats from topic `heart-beats-valid`, aggregates and computes heart rates and send them
> to `heart-rates` topic.
> This is kinda the heart of this whole project.

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
java -jar target/heart-rate-computor.jar

# run with docker in host network
docker run -it --rm --name heart-rate-computor --net host \
    -p 8380:8380 \
    linlouis/heart-rate-computor

# run with docker in the services network
docker run -it --rm --name heart-rate-computor --net "${PWD##*/}_default" \
    -p 8380:8380 \
    linlouis/heart-rate-computor \
    --spring.kafka.bootstrap-servers=kafka:29092 \
    --spring.kafka.properties.schema.registry.url=http://schema-registry:8081

# observe the topic "heart-rates"
docker exec -it schema-registry \
    /usr/bin/kafka-avro-console-consumer \
    --bootstrap-server kafka:29092 \
    --topic heart-rates \
    --from-beginning
```
