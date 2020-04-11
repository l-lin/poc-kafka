package lin.louis.poc.hbp.repository.kafa;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bakdata.schemaregistrymock.junit5.SchemaRegistryMockExtension;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import lin.louis.poc.hbp.repository.HBRepository;
import lin.louis.poc.hbp.repository.kafka.KafkaHBRepository;
import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


// Example from https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#example
@EmbeddedKafka(
		partitions = 1,
		topics = "heart-beats"
)
@ExtendWith(SpringExtension.class)
class KafkaHBRepositoryTest {

	public static final String TOPIC = "heart-beats";

	/**
	 * Since we are using Avro, we need a Schema Registry to fetch the Avro schemas. But I do not want to start up a
	 * Schema Registry just for unit tests, so we are using the "schema-registry-mock-junit5" that provides a really
	 * nice JUnit extension to start up a mocked Schema Registry.
	 *
	 * @see <a href="https://github.com/bakdata/fluent-kafka-streams-tests/tree/master/schema-registry-mock#as-a-standalone-module">Bakdata
	 * fluent-kafka-streams-test project</a>
	 */
	@RegisterExtension
	final SchemaRegistryMockExtension schemaRegistry = new SchemaRegistryMockExtension();

	@Autowired
	private EmbeddedKafkaBroker embeddedKafka;

	private HBRepository hbRepository;

	private BlockingQueue<ConsumerRecord<Long, HeartBeat>> consumerRecords;

	private KafkaMessageListenerContainer<Long, HeartBeat> container;

	@BeforeEach
	void setUp() {
		buildConsumer();
		buildProducer();
	}

	@AfterEach
	void tearDown() {
		container.stop();
	}

	@Test
	void send_shouldSendKafkaMessage() throws InterruptedException {
		// GIVEN
		var now = Instant.now();
		var heartBeat = new HeartBeat(123L, 80, HeartBeatQRS.A, now);

		// WHEN
		hbRepository.save(heartBeat);
		var received = consumerRecords.poll(10, TimeUnit.SECONDS);

		// THEN
		assertNotNull(received);
		var receivedValue = received.value();
		assertNotNull(receivedValue);
		assertAll(() -> {
			assertEquals(heartBeat.getUserId(), receivedValue.getUserId());
			assertEquals(heartBeat.getHri(), receivedValue.getHri());
			assertEquals(heartBeat.getQrs(), receivedValue.getQrs());
			assertEquals(heartBeat.getTimestamp(), receivedValue.getTimestamp());
		});
	}

	/**
	 * This consumer will help us check if the Kafka message has been sent correctly.
	 */
	private void buildConsumer() {
		var consumerProps = KafkaTestUtils.consumerProps("consumer", "false", embeddedKafka);
		consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
		consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SpecificAvroDeserializer.class);
		consumerProps.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry.getUrl());
		consumerRecords = new LinkedBlockingQueue<>();
		container = new KafkaMessageListenerContainer<>(
				new DefaultKafkaConsumerFactory<>(consumerProps),
				new ContainerProperties(TOPIC)
		);
		container.setupMessageListener((MessageListener<Long, HeartBeat>) record -> consumerRecords.add(record));
		container.start();
		ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
	}

	private void buildProducer() {
		// Using the helper provided by Spring KafkaTestUtils#producerProps to boilerplate the producer properties
		var producerProps = KafkaTestUtils.producerProps(embeddedKafka);
		// Use the right serializers for the topic key and value
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		// Since my HeartBeat Avro schema is specific (I'm using a specific timestamp), I need to use
		// SpecificAvroSerializer, not GenericAvroSerializer.
		// This is frequently a point of confusion. In the Java implementation, "generic" datum do not take into account
		// any customizations that were built into a specific record, including logical type conversions.
		// see https://stackoverflow.com/a/58390067/3612053
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SpecificAvroSerializer.class);
		producerProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry.getUrl());
		var template = new KafkaTemplate<>(new DefaultKafkaProducerFactory<Long, HeartBeat>(producerProps));
		hbRepository = new KafkaHBRepository(TOPIC, template);
	}
}
