# heart-rate-consumer

> Small webapp that reads Kafka topic "heart-rates" to display the heart rates in real-time in a
> graph, and another endpoint to display "old" heart rates for a given user.

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
java -jar target/heart-rate-consumer.jar

# run with docker in host network
docker run -it --rm --name heart-rate-consumer --net host \
    -p 8180:8180 \
    linlouis/heart-rate-consumer

# run with docker in the services network
docker run -it --rm --name heart-rate-consumer --net "${PWD##*/}_default" \
    -p 8180:8180 \
    linlouis/heart-rate-consumer \
    --spring.kafka.bootstrap-servers=kafka:29092 \
    --spring.kafka.properties.schema.registry.url=http://schema-registry:8081

# check out the website
firefox http://localhost:8480&
```

## Available endpoints

```bash
# follow the events in real-time
curl http://localhost:8480/users/1/heart-rates/stream

# get the last 60 seconds heart rates of user
curl http://localhost:8480/users/1/heart-rates?lastNSeconds=60

# get all users
curl http://localhost:8480/users
```

