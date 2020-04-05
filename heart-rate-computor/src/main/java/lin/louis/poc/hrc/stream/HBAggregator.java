package lin.louis.poc.hrc.stream;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.kafka.streams.kstream.Aggregator;

import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeats;


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
