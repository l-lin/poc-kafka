# heart-beat-validator

Service that streams heart beats from topic `heart-beats` to `heart-beats-valid` for valid heart beats and `heart-beats-invalid` for invalid ones.

## Getting started

```bash
# build maven project & build docker image
mvn clean package

# run in docker
# zookeeper, kafka & schema registry must be up
docker run -it --rm --name heart-beat-validator --net kafka-streams_default \
    -p 28080:8080 \
    linlouis/heart-beat-validator \
    --spring.kafka.bootstrap-servers=kafka1:9092,kafka2:9092,kafka3:9092 \
    --spring.kafka.properties.schema.registry.url=http://schema-registry:8081 \
    --topics.to.valid.partitions=1 \
    --topics.to.valid.replicas=3 \
    --topics.to.invalid.partitions=1 \
    --topics.to.invalid.replicas=3

# observe the topic "heart-beats-valid"
docker exec -it schema-registry \
    /usr/bin/kafka-avro-console-consumer \
    --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 \
    --topic heart-beats-valid \
    --from-beginning
# observe the topic "heart-beats-invalid"
docker exec -it schema-registry \
    /usr/bin/kafka-avro-console-consumer \
    --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 \
    --topic heart-beats-invalid \
    --from-beginning
```

## Resources

- [Spring Kafka Avro Streams example](https://github.com/gAmUssA/springboot-kafka-avro/blob/master/src/main/java/io/confluent/developer/kafkaworkshop/streams/KafkaStreamsApp.java)
- [Spring official documentation on Kafka Streams brancher](https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#using-kafkastreamsbrancher)
- [Spring example to use KafkaStreamBrancher](https://github.com/spring-projects/spring-kafka/blob/v2.3.7.RELEASE/spring-kafka/src/test/java/org/springframework/kafka/streams/KafkaStreamsBranchTests.java#L158-L166)
