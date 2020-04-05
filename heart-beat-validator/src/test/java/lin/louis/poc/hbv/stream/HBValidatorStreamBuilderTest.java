package lin.louis.poc.hbv.stream;

import java.time.Instant;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bakdata.fluent_kafka_streams_tests.junit5.TestTopologyExtension;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lin.louis.poc.hbv.predicate.ValidHBPredicate;
import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


@EmbeddedKafka(
		partitions = 1,
		topics = { "heart-beats", "heart-beats-valid", "heart-beats-invalid" }
)
@ExtendWith(SpringExtension.class)
class HBValidatorStreamBuilderTest {
	private static final String TOPIC_FROM = "heart-beats";
	private static final String TOPIC_TO_VALID = "heart-beats-valid";
	private static final String TOPIC_TO_INVALID = "heart-beats-invalid";
	private final Topology topology = HBValidatorStreamBuilder.withStreamsBuilder(new StreamsBuilder())
															  .from(TOPIC_FROM)
															  .to(TOPIC_TO_VALID, TOPIC_TO_INVALID)
															  .withPredicate(new ValidHBPredicate())
															  .buildTopology();
	@Autowired
	private EmbeddedKafkaBroker embeddedKafka;
	private TestTopologyExtension<Long, HeartBeat> testTopology;

	@BeforeEach
	void setUp() {
		testTopology = new TestTopologyExtension<>(topology, buildKafkaProperties(embeddedKafka));
		testTopology.start();
	}

	@AfterEach
	void tearDown() {
		if (testTopology != null) {
			testTopology.stop();
		}
	}

	@Test
	void shouldSendToCorrespondingTopic() {
		var validHeartBeats = new HeartBeat[] {
				new HeartBeat(101L, 50, HeartBeatQRS.A, Instant.now()),
				new HeartBeat(102L, 80, HeartBeatQRS.V, Instant.now()),
				new HeartBeat(103L, 90, HeartBeatQRS.F, Instant.now()),
				new HeartBeat(104L, 150, HeartBeatQRS.A, Instant.now()),
				new HeartBeat(105L, 5, HeartBeatQRS.P, Instant.now())
		};
		var invalidHeartBeats = new HeartBeat[] {
				new HeartBeat(-201L, 190, HeartBeatQRS.X, Instant.now()),
				new HeartBeat(-202L, 390, HeartBeatQRS.A, Instant.now()),
				new HeartBeat(-203L, -19, HeartBeatQRS.F, Instant.now())
		};
		testTopology.input(TOPIC_FROM)
					.add(validHeartBeats[0].getUserId(), validHeartBeats[0])
					.add(validHeartBeats[1].getUserId(), validHeartBeats[1])
					.add(validHeartBeats[2].getUserId(), validHeartBeats[2])
					.add(invalidHeartBeats[0].getUserId(), invalidHeartBeats[0])
					.add(invalidHeartBeats[1].getUserId(), invalidHeartBeats[1])
					.add(validHeartBeats[3].getUserId(), validHeartBeats[3])
					.add(invalidHeartBeats[2].getUserId(), null)
					.add(validHeartBeats[4].getUserId(), validHeartBeats[4]);

		testTopology.streamOutput(TOPIC_TO_VALID)
					.expectNextRecord().hasKey(validHeartBeats[0].getUserId()).hasValue(validHeartBeats[0])
					.expectNextRecord().hasKey(validHeartBeats[1].getUserId()).hasValue(validHeartBeats[1])
					.expectNextRecord().hasKey(validHeartBeats[2].getUserId()).hasValue(validHeartBeats[2])
					.expectNextRecord().hasKey(validHeartBeats[3].getUserId()).hasValue(validHeartBeats[3])
					.expectNextRecord().hasKey(validHeartBeats[4].getUserId()).hasValue(validHeartBeats[4])
					.expectNoMoreRecord();
		testTopology.streamOutput(TOPIC_TO_INVALID)
					.expectNextRecord().hasKey(invalidHeartBeats[0].getUserId()).hasValue(invalidHeartBeats[0])
					.expectNextRecord().hasKey(invalidHeartBeats[1].getUserId()).hasValue(invalidHeartBeats[1])
					// can't check null
					.expectNextRecord().hasKey(invalidHeartBeats[2].getUserId())
					.expectNoMoreRecord();
	}

	private Properties buildKafkaProperties(EmbeddedKafkaBroker embeddedKafka) {
		var properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "heart-beat-validator");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
		properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.LongSerde.class.getName());
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
		properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://dummy");
		return properties;
	}
}
