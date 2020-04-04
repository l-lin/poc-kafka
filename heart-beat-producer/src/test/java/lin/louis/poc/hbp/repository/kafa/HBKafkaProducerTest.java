package lin.louis.poc.hbp.repository.kafa;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
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
import lin.louis.poc.hbp.repository.kafka.HBKafkaProducer;
import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


// Example from https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#example
@EmbeddedKafka(
		partitions = 1,
		topics = "heart-beats"
)
@ExtendWith(SpringExtension.class)
class HBKafkaProducerTest {

	public static final String TEMPLATE_TOPIC = "heart-beats";

	@RegisterExtension
	SchemaRegistryMockExtension schemaRegistry = new SchemaRegistryMockExtension();

	@Autowired
	private EmbeddedKafkaBroker embeddedKafka;

	private HBRepository HBRepository;

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
		HBRepository.save(heartBeat);
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

	private void buildConsumer() {
		var consumerProps = KafkaTestUtils.consumerProps("consumer", "false", embeddedKafka);
		consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
		consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SpecificAvroDeserializer.class);
		consumerProps.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry.getUrl());
		consumerRecords = new LinkedBlockingQueue<>();
		container = new KafkaMessageListenerContainer<>(
				new DefaultKafkaConsumerFactory<>(consumerProps),
				new ContainerProperties(TEMPLATE_TOPIC)
		);
		container.setupMessageListener((MessageListener<Long, HeartBeat>) record -> consumerRecords.add(record));
		container.start();
		ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
	}

	private void buildProducer() {
		var producerProps = KafkaTestUtils.producerProps(embeddedKafka);
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SpecificAvroSerializer.class);
		producerProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry.getUrl());
		var template = new KafkaTemplate<>(new DefaultKafkaProducerFactory<Long, HeartBeat>(producerProps));
		HBRepository = new HBKafkaProducer(TEMPLATE_TOPIC, template);
	}
}
