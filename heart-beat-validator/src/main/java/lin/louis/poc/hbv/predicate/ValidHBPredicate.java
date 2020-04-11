package lin.louis.poc.hbv.predicate;

import org.apache.kafka.streams.kstream.Predicate;

import lin.louis.poc.models.HeartBeat;


/**
 * Just a simple predicate to check if a heart beat is valid or not. This predicate is simple in purpose, but we can
 * improve it especially if we want for example detect false positive heart beats.
 */
public class ValidHBPredicate implements Predicate<Long, HeartBeat> {

	@Override
	public boolean test(Long key, HeartBeat heartBeat) {
		return key > 0L && heartBeat != null;
	}
}
