# Example project that uses kafka streams

![Java](https://github.com/l-lin/poc-kafka/workflows/Java/badge.svg)
![Go](https://github.com/l-lin/poc-kafka/workflows/Go/badge.svg)

## Getting started
### Build

```bash
# this will build the maven projects and build the docker images.
mvn package
```

### Usage

__Production alike__

```bash
# launch all services
docker-compose up -d --scale heart-beat-producer=3 --scale heart-rate-computor=3
# wait until all services are started then setup environment
./scripts/setup.sh
```

__Local environment__

If you want to run apps directly from your IDE, you can't use the kafka cluster from docker-compose.yml because the
domain name configured in Kafka is only available for service in the docker network, which is not possible to hook
from IDE launched app. Thus, a `docker-compose-local.yml` file is here to launch a single kafka instance.

```bash
# launch all services
docker-compose -f docker-compose-dep.yml up -d
# then use same commands as above
```

## Useful commands

### Avro Schema Registry

More information on the [schema-registry project documentation](https://github.com/confluentinc/schema-registry).

The following examples use [HTTPie](https://httpie.org/) as the HTTP client to perform the HTTP requests:

```bash
# list all subjects
http :8081/subjects

# list all schema versions registered under the subject "heart-beats-value"
http :8081/subjects/heart-beats-value/versions

# fetch version 1 of the schema registered under the subject "heart-beats-value"
http :8081/subjects/heart-beats-value/versions/1

# fetch the most recently registered schema registered under the subject "heart-beats-value"
http :8081/subjects/heart-beats-value/versions/latest

# create heart beat avro schema in the schema registry
echo "{\"schema\":\"$(jq -c . < heart-models/src/main/resources/avro/HeartRate.avsc | sed 's/"/\\"/g')\"}" | http :8081/subjects/heart-rates-value/versions "Content-Type: application/vnd.schemaregistry.v1+json"

# create heart rate avro schema in the schema registry
echo "{\"schema\":\"$(jq -c . < heart-models/src/main/resources/avro/HeartRate.avsc | sed 's/"/\\"/g')\"}" | http :8081/subjects/heart-rates-value/versions "Content-Type: application/vnd.schemaregistry.v1+json"

# consume Avro messages to check what was sent to Kafka:
docker exec -it schema-registry \
    /usr/bin/kafka-avro-console-consumer \
    --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 \
    --topic heart-beats \
    --from-beginning
```

If breaking change, then the schema registry will throw a HTTP 409 and the application will get an error like:

```text
io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException: Schema being registered is incompatible with an earlier schema; error
 code: 409
```

More information by [Confluent on schema evolution and compatibility](https://docs.confluent.io/current/schema-registry/schema_registry_tutorial.html#schema-evolution-and-compatibility).

## Kafka connect

```bash
# list kafka connectors
http :8082/connectors

# add a new connector
curl -X "POST" "http://localhost:8082/connectors/" \
     -H "Content-Type: application/json" \
     -d '{
             "name": "heart-rate-connector-sink",
             "config": {
                 "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
                 "connection.url": "jdbc:postgresql://db:5432/heart_monitor?applicationName=heart-rate-connector",
                 "connection.user": "postgres",
                 "connection.password": "postgres",
                 "auto.create":"true",
                 "auto.evolve":"true",
                 "topics": "heart-rates",
                 "key.converter": "org.apache.kafka.connect.storage.StringConverter",
                 "transforms": "ExtractTimestamp",
                 "transforms.ExtractTimestamp.type": "org.apache.kafka.connect.transforms.InsertField$Value",
                 "transforms.ExtractTimestamp.timestamp.field" : "extract_ts"
             }
     }'
```

## Resources

__Kafka__

- [Confluent on testing streaming application](https://www.confluent.io/blog/stream-processing-part-2-testing-your-streaming-application/)
- [The internal of Kafka Gitbook](https://jaceklaskowski.gitbooks.io/apache-kafka/)
- [The internal of Kafka Streams Gitbook](https://jaceklaskowski.gitbooks.io/mastering-kafka-streams/)
- [Example of using Kafka Streams API](https://github.com/abhirockzz/kafka-streams-apis)
- [Exploring Kafka Streams](https://dev.to/itnext/learn-stream-processing-with-kafka-streams-stateless-operations-1k4h)
- [Reactor Kafka](https://projectreactor.io/docs/kafka/release/reference/)
- [Introduction to reactor Kafka](https://www.reactiveprogramming.be/an-introduction-to-reactor-kafka/)
- [How to choose number of topic partition](https://www.confluent.io/blog/how-choose-number-topics-partitions-kafka-cluster/)
- [Kafka replication explained](https://www.confluent.io/blog/hands-free-kafka-replication-a-lesson-in-operational-simplicity/)

__Avro__

- [Avro and the schema registry](https://aseigneurin.github.io/2018/08/02/kafka-tutorial-4-avro-and-schema-registry.html)
- [Avro specifications](https://avro.apache.org/docs/1.8.1/spec.html#schemas)
- [CodeNotFound Spring Kafka - Apache Avro serializer / deserializer example](https://codenotfound.com/spring-kafka-apache-avro-serializer-deserializer-example.html)
- [Confluent documentation on schema registry](https://docs.confluent.io/current/schema-registry/schema_registry_tutorial.html)
- [Stackoverflow question on testing Kafka consumer with Avro schema](https://stackoverflow.com/questions/57575067/kafka-consumer-unit-test-with-avro-schema-registry-failing)
- [Bakdata fluent-kafka-streams-test to test Kafka streams](https://github.com/bakdata/fluent-kafka-streams-tests)

__Spring__

- [Spring Kafka](https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#kafka)
- [How to transform a batch pipeline into real time one](https://medium.com/@stephane.maarek/how-to-use-apache-kafka-to-transform-a-batch-pipeline-into-a-real-time-one-831b48a6ad85)
- [Using Apache Kafka and Spring platform to build event-driven microservices](https://gamov.io/workshop/cnfl-pivotal-ord-2020.html#adding-avro-and-confluent-schema-registry-dependencies)
- [Spring Kafka Avro without registry for unit tests](https://github.com/ivlahek/kafka-avro-without-registry)
- [Spring Kafka Avro Streams example](https://github.com/gAmUssA/springboot-kafka-avro/blob/master/src/main/java/io/confluent/developer/kafkaworkshop/streams/KafkaStreamsApp.java)
- [Spring official documentation on Kafka Streams brancher](https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#using-kafkastreamsbrancher)
- [Spring example to use KafkaStreamBrancher](https://github.com/spring-projects/spring-kafka/blob/v2.3.7.RELEASE/spring-kafka/src/test/java/org/springframework/kafka/streams/KafkaStreamsBranchTests.java#L158-L166)
- [Spring tutorial for building interactive web app using websocket](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [Spring tutorial for accessing data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

__Reactor__

- [Spring Webflux and SSE](https://josdem.io/techtalk/spring/spring_boot_sse/)
- [Yong Mook Kim Spring Webflux and SSE tutorial](https://mkyong.com/spring-boot/spring-boot-webflux-server-sent-events-example/)
- [Okta tutorial on getting started with Reactive programming in Spring](https://developer.okta.com/blog/2018/09/21/reactive-programming-with-spring)
- [Stream realtime data the reactive way with Angular+Spring boot+Kafka](https://medium.com/swlh/angular-spring-boot-kafka-how-to-stream-realtime-data-the-reactive-way-510a0f1e5881)
- [Sample reactive app in Spring](https://github.com/CollaborationInEncapsulation/get-reactive-with-spring5-demo)
- [Samples from official Reactor Kafka project](https://github.com/reactor/reactor-kafka/tree/master/reactor-kafka-samples)
- [Another sample using Reactor Kafka](https://github.com/davemaier/reactivekafkaserver)
- [WebFluxTest with WebTestClient](https://howtodoinjava.com/spring-webflux/webfluxtest-with-webtestclient/)
- [Testing with reactor-test in JUnit](https://projectreactor.io/docs/core/release/reference/index.html#testing)

__KSQL__

- [KSQL workshop](https://github.com/confluentinc/demo-scene/blob/master/ksql-workshop/ksql-workshop.adoc)
- [ksqDB reference doc](https://docs.ksqldb.io/en/latest/developer-guide/ksqldb-reference/select-pull-query/)

__Front__

- [Bulma - CSS framework](https://bulma.io/)
- [Flot - JS plotting library](https://www.flotcharts.org/)