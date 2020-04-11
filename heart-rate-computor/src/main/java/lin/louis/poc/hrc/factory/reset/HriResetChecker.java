package lin.louis.poc.hrc.factory.reset;

import java.util.Queue;

import lin.louis.poc.models.HeartBeat;


/**
 * Checking if the heartbeat has a valid Instant Heart Rate and should not be out of range.
 */
public class HriResetChecker implements ResetChecker {

	private final int hriMin;

	private final int hriMax;

	public HriResetChecker(int hriMin, int hriMax) {
		this.hriMin = hriMin;
		this.hriMax = hriMax;
	}

	@Override
	public boolean isReset(Queue<HeartBeat> heartBeatList) {
		return heartBeatList.stream()
							.anyMatch(heartBeat -> heartBeat.getHri() < hriMin || heartBeat.getHri() > hriMax);
	}
}
