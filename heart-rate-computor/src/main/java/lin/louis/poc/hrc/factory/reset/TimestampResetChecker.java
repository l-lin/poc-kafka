package lin.louis.poc.hrc.factory.reset;

import java.util.ArrayList;
import java.util.Queue;

import lin.louis.poc.models.HeartBeat;


/**
 * Checking if a heartbeat has a timestamp before the last heartbeat timestamp.
 */
public class TimestampResetChecker implements ResetChecker {

	@Override
	public boolean isReset(Queue<HeartBeat> heartBeatQueue) {
		var heartBeatList = new ArrayList<>(heartBeatQueue);
		for (var i = 0; i < heartBeatList.size() - 1; i++) {
			var first = heartBeatList.get(i).getTimestamp();
			var second = heartBeatList.get(i + 1).getTimestamp();
			if (first.isAfter(second)) {
				return true;
			}
		}
		return false;
	}
}
