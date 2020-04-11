package lin.louis.poc.hrc.stream;

import java.time.Instant;
import java.util.ArrayList;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;

import lin.louis.poc.hrc.factory.HBInputBuilder;
import lin.louis.poc.hrc.factory.HRFactory;
import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeats;
import lin.louis.poc.models.HeartRate;


public class HRValueMapper implements ValueMapperWithKey<Long, HeartBeats, Iterable<HeartRate>> {

	private final HRFactory hrFactory;

	private final int nbHeartBeats;

	private Instant heartBeatOffset;

	public HRValueMapper(HRFactory hrFactory, int nbHeartBeats) {
		this.hrFactory = hrFactory;
		this.nbHeartBeats = nbHeartBeats;
		this.heartBeatOffset = Instant.MIN;
	}

	@Override
	public Iterable<HeartRate> apply(Long userId, HeartBeats value) {
		var heartRates = new ArrayList<HeartRate>();

		var heartBeats = HBInputBuilder.from(value.getHeartBeats())
									   .withNbHeartBeats(nbHeartBeats)
									   .withOffset(heartBeatOffset)
									   .build();
		var heartBeatQueue = new CircularFifoQueue<HeartBeat>(nbHeartBeats);

		if (heartBeats.size() <= nbHeartBeats) {
			heartBeatQueue.addAll(heartBeats);
			heartRates.add(hrFactory.create(userId, heartBeatQueue));
		} else {
			for (var i = 0; i < nbHeartBeats; i++) {
				heartBeatQueue.add(heartBeats.get(i));
			}
			heartRates.add(hrFactory.create(userId, heartBeatQueue));
			var i = nbHeartBeats;
			while (i < heartBeats.size()) {
				heartBeatQueue.add(heartBeats.get(i));
				heartRates.add(hrFactory.create(userId, heartBeatQueue));
				i++;
			}
		}
		heartBeatOffset = heartRates.get(heartRates.size() - 1).getTimestamp();
		return heartRates;
	}

}
