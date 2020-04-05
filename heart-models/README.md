# heart-models

> Common library that contains the [Avro](https://avro.apache.org/docs/1.8.1/spec.html#schemas) schemas of all models
> required for this project.

```bash
# generate class files (will be located under target/generated-sources folder)
mvn generate-sources

# check the Avro schema files against the schema registry
# can be useful when using in a CI
mvn io.confluent:kafka-schema-registry-maven-plugin:5.4.1:test-compatibility
```
