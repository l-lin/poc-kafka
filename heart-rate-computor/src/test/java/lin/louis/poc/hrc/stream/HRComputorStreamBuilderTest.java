package lin.louis.poc.hrc.stream;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;
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

import com.bakdata.fluent_kafka_streams_tests.TestInput;
import com.bakdata.fluent_kafka_streams_tests.TestOutput;
import com.bakdata.fluent_kafka_streams_tests.junit5.TestTopologyExtension;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lin.louis.poc.hrc.factory.HRFactory;
import lin.louis.poc.hrc.factory.reset.GapResetChecker;
import lin.louis.poc.hrc.factory.reset.HriResetChecker;
import lin.louis.poc.hrc.factory.reset.QRSResetChecker;
import lin.louis.poc.hrc.factory.reset.ResetCheckerFacade;
import lin.louis.poc.hrc.factory.reset.TimestampResetChecker;
import lin.louis.poc.hrc.factory.valuecomputor.HRValueComputor;
import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;
import lin.louis.poc.models.HeartRate;


@EmbeddedKafka(
		partitions = 1,
		topics = { "heart-beats-valid", "heart-rates" }
)
@ExtendWith(SpringExtension.class)
class HRComputorStreamBuilderTest {
	private static final long USER_ID = 123L;
	private static final String TOPIC_FROM = "heart-beats-valid";
	private static final String TOPIC_TO = "heart-rates";
	private final HRFactory hrFactory = new HRFactory(
			8,
			new ResetCheckerFacade(Arrays.asList(
					new GapResetChecker(Duration.ofSeconds(5)),
					new HriResetChecker(0, 250),
					new QRSResetChecker(),
					new TimestampResetChecker()
			)),
			new HRValueComputor()
	);
	private final Topology topology = HRComputorStreamBuilder.withStreamsBuilder(new StreamsBuilder())
															 .from(TOPIC_FROM)
															 .to(TOPIC_TO)
															 .withHRFactory(hrFactory)
															 .heartRateComputedBy(8)
															 .buildTopology();
	@Autowired
	private EmbeddedKafkaBroker embeddedKafka;
	private TestTopologyExtension<Long, HeartRate> testTopology;

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
		// GIVEN
		var heartBeats = Arrays.asList(
				new HeartBeat(USER_ID, 80, HeartBeatQRS.N, newInstant(1)),
				new HeartBeat(USER_ID, 100, HeartBeatQRS.V, newInstant(2)),
				new HeartBeat(USER_ID, 83, HeartBeatQRS.N, newInstant(3)),
				new HeartBeat(USER_ID, 80, HeartBeatQRS.P, newInstant(4)),
				new HeartBeat(USER_ID, 91, HeartBeatQRS.A, newInstant(5)),
				new HeartBeat(USER_ID, 88, HeartBeatQRS.N, newInstant(7)),
				new HeartBeat(USER_ID, 70, HeartBeatQRS.N, newInstant(8)),
				new HeartBeat(USER_ID, 10, HeartBeatQRS.F, newInstant(10)), // 8
				new HeartBeat(USER_ID, 90, HeartBeatQRS.F, newInstant(11)),
				new HeartBeat(USER_ID, 201, HeartBeatQRS.A, newInstant(12)),
				new HeartBeat(USER_ID, 88, HeartBeatQRS.A, newInstant(15)),
				new HeartBeat(USER_ID, 222, HeartBeatQRS.V, newInstant(17)),
				new HeartBeat(USER_ID, 89, HeartBeatQRS.P, newInstant(18)),
				new HeartBeat(USER_ID, 100, HeartBeatQRS.F, newInstant(19)),
				new HeartBeat(USER_ID, 101, HeartBeatQRS.F, newInstant(20))
		);

		// WHEN
		var heartBeatSerde = new SpecificAvroSerde<HeartBeat>(testTopology.getSchemaRegistry());
		heartBeatSerde.configure(testTopology.getStreamsConfig().originals(), false);
		TestInput<Long, HeartBeat> testInput = testTopology.input(TOPIC_FROM)
														   .withKeySerde(new Serdes.LongSerde())
														   .withValueSerde(heartBeatSerde);
		heartBeats.forEach(heartBeat -> testInput.add(heartBeat.getUserId(), heartBeat));

		// THEN
		TestOutput<Long, HeartRate> testOutput = testTopology.streamOutput(TOPIC_TO);
		var heartRates = Arrays.asList(
				new HeartRate(USER_ID, Double.NaN, heartBeats.get(0).getTimestamp(), false),
				new HeartRate(USER_ID, Double.NaN, heartBeats.get(1).getTimestamp(), false),
				new HeartRate(USER_ID, Double.NaN, heartBeats.get(2).getTimestamp(), false),
				new HeartRate(USER_ID, Double.NaN, heartBeats.get(3).getTimestamp(), false),
				new HeartRate(USER_ID, Double.NaN, heartBeats.get(4).getTimestamp(), false),
				new HeartRate(USER_ID, Double.NaN, heartBeats.get(5).getTimestamp(), false),
				new HeartRate(USER_ID, Double.NaN, heartBeats.get(6).getTimestamp(), false),
				new HeartRate(USER_ID, 81.5, heartBeats.get(7).getTimestamp(), false),
				new HeartRate(USER_ID, 85.5, heartBeats.get(8).getTimestamp(), false),
				new HeartRate(USER_ID, 85.5, heartBeats.get(9).getTimestamp(), false),
				new HeartRate(USER_ID, 88d, heartBeats.get(10).getTimestamp(), false),
				new HeartRate(USER_ID, 89d, heartBeats.get(11).getTimestamp(), false),
				new HeartRate(USER_ID, 88.5, heartBeats.get(12).getTimestamp(), false),
				new HeartRate(USER_ID, 89.5, heartBeats.get(13).getTimestamp(), false),
				new HeartRate(USER_ID, 95d, heartBeats.get(14).getTimestamp(), false)
		);
		heartRates.forEach(heartRate -> testOutput.expectNextRecord()
												  .hasKey(heartRate.getUserId())
												  .hasValue(heartRate));
		testOutput.expectNoMoreRecord();
	}

	private Properties buildKafkaProperties(EmbeddedKafkaBroker embeddedKafka) {
		var properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "heart-rate-computor");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
		properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.LongSerde.class.getName());
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
		properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://dummy");
		return properties;
	}

	private Instant newInstant(int seconds) {
		return LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, seconds).toInstant(ZoneOffset.UTC);
	}
}