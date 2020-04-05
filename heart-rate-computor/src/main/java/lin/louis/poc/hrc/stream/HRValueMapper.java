package lin.louis.poc.hrc.stream;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;

import lin.louis.poc.hrc.usecase.HRFactory;
import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeats;
import lin.louis.poc.models.HeartRate;


public class HRValueMapper implements ValueMapperWithKey<Long, HeartBeats, HeartRate> {
	private final HRFactory hrFactory;
	private final int nbHeartBeats;

	public HRValueMapper(HRFactory hrFactory, int nbHeartBeats) {
		this.hrFactory = hrFactory;
		this.nbHeartBeats = nbHeartBeats;
	}

	@Override
	public HeartRate apply(Long userId, HeartBeats value) {
		var heartBeats = value.getHeartBeats();
		var heartBeatQueue = new CircularFifoQueue<HeartBeat>(nbHeartBeats);
		if (heartBeats.size() <= nbHeartBeats) {
			heartBeatQueue.addAll(heartBeats);
		} else {
			heartBeatQueue.addAll(heartBeats.subList(
					heartBeats.size() - nbHeartBeats - 1,
					heartBeats.size()
			));
		}
		return hrFactory.create(userId, heartBeatQueue);
	}
}
