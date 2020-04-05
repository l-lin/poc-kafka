package lin.louis.poc.hbv.predicate;

import org.apache.kafka.streams.kstream.Predicate;

import lin.louis.poc.models.HeartBeat;


public class ValidHBPredicate implements Predicate<Long, HeartBeat> {

	@Override
	public boolean test(Long key, HeartBeat heartBeat) {
		return key > 0L && heartBeat != null;
	}
}
