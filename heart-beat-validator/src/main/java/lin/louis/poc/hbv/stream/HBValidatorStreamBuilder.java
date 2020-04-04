package lin.louis.poc.hbv.stream;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Printed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.KafkaStreamBrancher;

import lin.louis.poc.models.HeartBeat;


public class HBValidatorStreamBuilder {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final StreamsBuilder streamsBuilder;

	private String topicFrom;

	private String topicValidTo;

	private String topicInvalidTo;

	private Predicate<Long, HeartBeat> predicate;

	private HBValidatorStreamBuilder(StreamsBuilder streamsBuilder) {this.streamsBuilder = streamsBuilder;}

	public static HBValidatorStreamBuilder withStreamsBuilder(StreamsBuilder streamsBuilder) {
		return new HBValidatorStreamBuilder(streamsBuilder);
	}

	public HBValidatorStreamBuilder from(String topicFrom) {
		this.topicFrom = topicFrom;
		return this;
	}

	public HBValidatorStreamBuilder to(String topicValidTo, String topicInvalidTo) {
		this.topicValidTo = topicValidTo;
		this.topicInvalidTo = topicInvalidTo;
		return this;
	}

	public HBValidatorStreamBuilder withPredicate(Predicate<Long, HeartBeat> predicate) {
		this.predicate = predicate;
		return this;
	}

	public KStream<Long, HeartBeat> buildKStream() {
		var from = streamsBuilder.<Long, HeartBeat>stream(topicFrom)
				.peek((key, heartBeat) -> logger.info("reading heart beat with key '{}': '{}'", key, heartBeat));
		from.print(Printed.toSysOut());
		return new KafkaStreamBrancher<Long, HeartBeat>()
				.branch(predicate, kStream -> kStream.to(topicValidTo))
				.defaultBranch(kStream -> kStream.to(topicInvalidTo))
				.onTopOf(from);
	}

	public Topology buildTopology() {
		buildKStream();
		return streamsBuilder.build();
	}
}
