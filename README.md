# Example project that uses kafka streams

__TODO__

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
```

If breaking change, then the schema registry will throw a HTTP 409 and the application will get an error like:

```text
io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException: Schema being registered is incompatible with an earlier schema; error
 code: 409
```

More information by [Confluent on schema evolution and compatility](https://docs.confluent.io/current/schema-registry/schema_registry_tutorial.html#schema-evolution-and-compatibility).

## Resources

__Spring Kafka__

- [Spring Kafka](https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#kafka)
- [How to transform a batch pipeline into real time one](https://medium.com/@stephane.maarek/how-to-use-apache-kafka-to-transform-a-batch-pipeline-into-a-real-time-one-831b48a6ad85)
- [Using Apache Kafka and Spring platform to build event-driven microservices](https://gamov.io/workshop/cnfl-pivotal-ord-2020.html#adding-avro-and-confluent-schema-registry-dependencies)

__Avro__

- [Avro and the schema registry](https://aseigneurin.github.io/2018/08/02/kafka-tutorial-4-avro-and-schema-registry.html)
- [Avro specifications](https://avro.apache.org/docs/1.8.1/spec.html#schemas)
- [CodeNotFound Spring Kafka - Apache Avro serializer / deserializer example](https://codenotfound.com/spring-kafka-apache-avro-serializer-deserializer-example.html)
- [Confluent documentation on schema registry](https://docs.confluent.io/current/schema-registry/schema_registry_tutorial.html)