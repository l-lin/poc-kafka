package lin.louis.poc.hrc.repository.kafka;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.core.BrokerAddress;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bakdata.schemaregistrymock.junit5.SchemaRegistryMockExtension;

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import lin.louis.poc.hrc.repository.HRFluxRepository;
import lin.louis.poc.models.HeartRate;
import reactor.test.StepVerifier;


@EmbeddedKafka(
		partitions = 1,
		topics = "heart-rates"
)
@ExtendWith(SpringExtension.class)
class KafkaHRFluxRepositoryTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String TOPIC = "heart-rates";

	private static final long USER_ID = 123L;

	@RegisterExtension
	SchemaRegistryMockExtension schemaRegistry = new SchemaRegistryMockExtension();

	@Autowired
	private EmbeddedKafkaBroker embeddedKafka;

	private KafkaTemplate<Long, HeartRate> kafkaTemplate;

	private HRFluxRepository hrFluxRepository;

	@BeforeEach
	void setUp() {
		// CONSUMER
		var kafkaProperties = new KafkaProperties();
		kafkaProperties.setBootstrapServers(Arrays.stream(embeddedKafka.getBrokerAddresses())
												  .map(BrokerAddress::toString)
												  .collect(Collectors.toList()));
		kafkaProperties.getProperties().put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry.getUrl());
		kafkaProperties.getProperties().put(SPECIFIC_AVRO_READER_CONFIG, "true");
		kafkaProperties.getConsumer().setGroupId("heart-rate-consumer");
		kafkaProperties.getConsumer().setKeyDeserializer(LongDeserializer.class);
		kafkaProperties.getConsumer().setValueDeserializer(SpecificAvroDeserializer.class);
		// need to set to earliest, because we send the kafka message first, before reading
		kafkaProperties.getConsumer().setAutoOffsetReset("earliest");
		hrFluxRepository = new KafkaHRFluxRepository(kafkaProperties);

		// PRODUCER
		var producerProps = KafkaTestUtils.producerProps(embeddedKafka);
		producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
		producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SpecificAvroSerializer.class);
		producerProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry.getUrl());
		kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps));
	}

	@Test
	void read_shouldReadKafkaMessages() {
		// GIVEN
		var heartRates = Arrays.asList(
				new HeartRate(USER_ID, 90d, Instant.now(), false),
				new HeartRate(USER_ID, 91d, Instant.now(), false),
				new HeartRate(USER_ID, Double.NaN, Instant.now(), false)
		);
		heartRates.forEach(heartRate -> kafkaTemplate.send(TOPIC, USER_ID, heartRate));

		// WHEN
		var step = StepVerifier.create(hrFluxRepository.read(USER_ID));

		// THEN
		heartRates.forEach(heartRate -> {
			step.assertNext(heartRateRead -> {
				assertNotNull(heartRateRead);
				logger.info("Read heart rate: {}", heartRateRead);
				assertEquals(USER_ID, heartRateRead.getUserId());
				assertEquals(heartRate.getValue(), heartRateRead.getValue());
				assertEquals(heartRate.getTimestamp(), heartRateRead.getTimestamp());
				assertEquals(heartRate.getIsReset(), heartRateRead.getIsReset());
			});
		});
		step.verifyTimeout(Duration.ofSeconds(1));
	}
}
