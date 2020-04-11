package lin.louis.poc.hrc.factory.reset;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Queue;

import lin.louis.poc.models.HeartBeat;


/**
 * Check if the heart rate as a gap, which is detected when there is N seconds or more between two consecutives
 * heartbeats.
 */
public class GapResetChecker implements ResetChecker {

	private final Duration gapDuration;

	public GapResetChecker(Duration gapDuration) {this.gapDuration = gapDuration;}

	@Override
	public boolean isReset(Queue<HeartBeat> heartBeats) {
		var heartBeatList = new ArrayList<>(heartBeats);
		for (int i = 0; i < heartBeatList.size() - 1; i++) {
			var first = heartBeatList.get(i).getTimestamp();
			var second = heartBeatList.get(i + 1).getTimestamp();
			if (first.plus(gapDuration).isBefore(second)) {
				return true;
			}
		}
		return false;
	}
}
