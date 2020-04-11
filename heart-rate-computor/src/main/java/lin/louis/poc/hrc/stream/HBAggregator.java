package lin.louis.poc.hrc.stream;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.kafka.streams.kstream.Aggregator;

import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeats;


/**
 * Aggregate a list of {@link lin.louis.poc.models.HeartBeat} into a {@link lin.louis.poc.models.HeartBeats}. We can't
 * directly use a {@link java.lang.Iterable<lin.louis.poc.models.HeartBeat>} because it seems to serialize & deserialize
 * in between the kafka streams, and since it's not an Avro schema, it will miserably fail... The {@link
 * lin.louis.poc.models.HeartBeats} is a small hack to bypass this small inconvenience.
 */
public enum HBAggregator implements Aggregator<Long, HeartBeat, HeartBeats> {
	INSTANCE;

	@Override
	public HeartBeats apply(Long userId, HeartBeat heartBeat, HeartBeats heartBeats) {
		var list = Optional.ofNullable(heartBeats.getHeartBeats()).orElseGet(ArrayList::new);
		list.add(heartBeat);
		heartBeats.setUserId(userId);
		heartBeats.setHeartBeats(list);
		return heartBeats;
	}
}
