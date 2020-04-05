package lin.louis.poc.hrc.usecase;

import java.time.Instant;
import java.util.Queue;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lin.louis.poc.hrc.usecase.reset.ResetCheckerFacade;
import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartRate;


/**
 * Create heart rate from given heart beats
 */
public class HRFactory {

	private static final BiFunction<Long, Instant, HeartRate> NAN =
			(userId, instant) -> new HeartRate(userId, Double.NaN, instant, false);
	private static final BiFunction<Long, Instant, HeartRate> RESET =
			(userId, instant) -> new HeartRate(userId, Double.NaN, instant, true);
	private final int nbHeartBeats;
	private final ResetCheckerFacade resetCheckerFacade;
	private final HRValueComputor hrValueComputor;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public HRFactory(
			int nbHeartBeats,
			ResetCheckerFacade resetCheckerFacade,
			HRValueComputor hrValueComputor
	) {
		this.nbHeartBeats = nbHeartBeats;
		this.resetCheckerFacade = resetCheckerFacade;
		this.hrValueComputor = hrValueComputor;
	}

	public HeartRate create(long userId, Queue<HeartBeat> heartBeats) {
		if (heartBeats.isEmpty()) {
			logger.debug("Cannot create HeartRate as there are no heart beats detected");
			return NAN.apply(userId, Instant.now());
		}
		var lastHeartBeat = getLastHeartBeat(heartBeats);
		var t = lastHeartBeat.getTimestamp();
		if (!hasEnoughHeartBeats(heartBeats)) {
			logger.debug("There are not enough heart beats ({}) to create a heart rate (need {} heart beats) at {}",
						 heartBeats.size(),
						 nbHeartBeats, t
			);
			return NAN.apply(userId, t);
		}
		if (resetCheckerFacade.isReset(heartBeats)) {
			logger.debug("Reset at {}", t);
			return RESET.apply(userId, t);
		}

		var value = hrValueComputor.compute(heartBeats.stream()
													  .mapToInt(HeartBeat::getHri)
													  .toArray());
		return new HeartRate(userId, value, t, false);
	}

	/**
	 * Not the best way... but since it's only queue of size 8, it should be ok, performance wise
	 *
	 * @param heartBeats the heart beats
	 * @return the last element of the queue
	 */
	private HeartBeat getLastHeartBeat(Queue<HeartBeat> heartBeats) {
		return heartBeats.stream().reduce((a, b) -> b).orElse(heartBeats.peek());
	}

	private boolean hasEnoughHeartBeats(Queue<HeartBeat> heartBeatList) {
		return nbHeartBeats <= heartBeatList.size();
	}
}
