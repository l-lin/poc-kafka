package lin.louis.poc.hbv.predicate;

import org.apache.kafka.streams.kstream.Predicate;

import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


public class ValidHeartBeatPredicate implements Predicate<Long, HeartBeat> {

	private final int hriMin;

	private final int hriMax;

	public ValidHeartBeatPredicate(int hriMin, int hriMax) {
		this.hriMin = hriMin;
		this.hriMax = hriMax;
	}

	@Override
	public boolean test(Long key, HeartBeat heartBeat) {
		return HeartBeatQRS.X != heartBeat.getQrs() && heartBeat.getHri() >= hriMin && heartBeat.getHri() <= hriMax;
	}
}
