# Example project that uses kafka streams

## Getting started
### Build

```bash
# this will build the maven projects and build the docker images.
mvn package
```

### Usage
 
```bash
# launch all services
docker-compose up


# if you want to run apps directly from your IDEA, you can't use the kafka cluster from docker-compose.yml because the
# domain name configured in Kafka is only available for service in the docker network, which is not possible to hook
# from IDE launched app. Thus, a docker-compose-local.yml file is here to launch a single kafka instance.
docker-compose -f docker-compose-local.yml up
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

## Resources

__Kafka__

- [Confluent on testing streaming application](https://www.confluent.io/blog/stream-processing-part-2-testing-your-streaming-application/)
- [The internal of Kafka Gitbook](https://jaceklaskowski.gitbooks.io/apache-kafka/)
- [The internal of Kafka Streams Gitbook](https://jaceklaskowski.gitbooks.io/mastering-kafka-streams/)

__Avro__

- [Avro and the schema registry](https://aseigneurin.github.io/2018/08/02/kafka-tutorial-4-avro-and-schema-registry.html)
- [Avro specifications](https://avro.apache.org/docs/1.8.1/spec.html#schemas)
- [CodeNotFound Spring Kafka - Apache Avro serializer / deserializer example](https://codenotfound.com/spring-kafka-apache-avro-serializer-deserializer-example.html)
- [Confluent documentation on schema registry](https://docs.confluent.io/current/schema-registry/schema_registry_tutorial.html)
- [Stackoverflow question on testing Kafka consumer with Avro schema](https://stackoverflow.com/questions/57575067/kafka-consumer-unit-test-with-avro-schema-registry-failing)
- [Bakdata fluent-kafka-streams-test to test Kafka streams](https://github.com/bakdata/fluent-kafka-streams-tests)

__Spring Kafka__

- [Spring Kafka](https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#kafka)
- [How to transform a batch pipeline into real time one](https://medium.com/@stephane.maarek/how-to-use-apache-kafka-to-transform-a-batch-pipeline-into-a-real-time-one-831b48a6ad85)
- [Using Apache Kafka and Spring platform to build event-driven microservices](https://gamov.io/workshop/cnfl-pivotal-ord-2020.html#adding-avro-and-confluent-schema-registry-dependencies)
- [Spring Kafka Avro without registry for unit tests](https://github.com/ivlahek/kafka-avro-without-registry)
- [Spring Kafka Avro Streams example](https://github.com/gAmUssA/springboot-kafka-avro/blob/master/src/main/java/io/confluent/developer/kafkaworkshop/streams/KafkaStreamsApp.java)
- [Spring official documentation on Kafka Streams brancher](https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#using-kafkastreamsbrancher)
- [Spring example to use KafkaStreamBrancher](https://github.com/spring-projects/spring-kafka/blob/v2.3.7.RELEASE/spring-kafka/src/test/java/org/springframework/kafka/streams/KafkaStreamsBranchTests.java#L158-L166)

__KSQL__

- [KSQL workshop](https://github.com/confluentinc/demo-scene/blob/master/ksql-workshop/ksql-workshop.adoc)
- [ksqDB reference doc](https://docs.ksqldb.io/en/latest/developer-guide/ksqldb-reference/select-pull-query/)
