package lin.louis.poc.hrc.stream;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lin.louis.poc.hrc.factory.HRFactory;
import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeats;
import lin.louis.poc.models.HeartRate;


public class HRComputorStreamBuilder {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final StreamsBuilder streamsBuilder;

	private String topicFrom;

	private String topicTo;

	private HRFactory hrFactory;

	private int nbHeartBeats;

	private HRComputorStreamBuilder(StreamsBuilder streamsBuilder) {
		this.streamsBuilder = streamsBuilder;
	}

	public static HRComputorStreamBuilder withStreamsBuilder(StreamsBuilder streamsBuilder) {
		return new HRComputorStreamBuilder(streamsBuilder);
	}

	public HRComputorStreamBuilder from(String topicFrom) {
		this.topicFrom = topicFrom;
		return this;
	}

	public HRComputorStreamBuilder to(String topicTo) {
		this.topicTo = topicTo;
		return this;
	}

	public HRComputorStreamBuilder withHRFactory(HRFactory hrFactory) {
		this.hrFactory = hrFactory;
		return this;
	}

	public HRComputorStreamBuilder heartRateComputedBy(int nbHeartBeats) {
		this.nbHeartBeats = nbHeartBeats;
		return this;
	}

	public KStream<Long, HeartRate> buildKStream() {
		// read valid heart beats from kafka
		KStream<Long, HeartBeat> kStream = streamsBuilder.<Long, HeartBeat>stream(topicFrom)
				.peek((userId, heartBeat) -> logger.debug("reading heart beat of user {}: {}", userId, heartBeat));

		// KTable that will contain the aggregated heart beats.
		// I had to create a new HeartBeats Avro model, because Kafka does not know how to deserialize Iterable of
		// Avro models...
		// We could use a TimeWindow to fetch only current window, not every heart beats from this KTable
		KTable<Long, HeartBeats> kTable = kStream
				.groupByKey()
				.aggregate(HeartBeats::new, HBAggregator.INSTANCE, Materialized.as("aggregated-heart-beats"));

		// map HeartBeats into HeartRates
		KStream<Long, HeartRate> outKStream = kTable
				.toStream()
				// /!\ this will only work with kafka in cluster as it needs the property "processing.guarantee=exactly_once"
				// to ensure we have the expected behavior, i.e. fetching the values from the KTable and computing
				// directly in "real time". When the property "processing.guarantee" is set to "at_least_once", the
				// mapping will not be performed directly, and there is a small time buffer before it performs the
				// mapping, hence having weird behavior, like having too many heartbeats for a single heart rate, or
				// having heart beats with offset timestamps...
				.flatMapValues(new HRValueMapper(hrFactory, nbHeartBeats))
				.peek((userId, heartRate) -> logger.debug("heart rate computed for user {}: {}", userId, heartRate));
		outKStream.to(topicTo);
		return outKStream;
	}

	public Topology buildTopology() {
		buildKStream();
		return streamsBuilder.build();
	}
}
