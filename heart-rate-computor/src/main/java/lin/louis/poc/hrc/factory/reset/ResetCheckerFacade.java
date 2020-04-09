package lin.louis.poc.hrc.factory.reset;

import java.util.List;
import java.util.Queue;

import lin.louis.poc.models.HeartBeat;


public class ResetCheckerFacade {

	private final List<ResetChecker> resetCheckers;

	public ResetCheckerFacade(List<ResetChecker> resetCheckers) {this.resetCheckers = resetCheckers;}

	public boolean isReset(Queue<HeartBeat> heartBeatList) {
		return resetCheckers.stream().anyMatch(checker -> checker.isReset(heartBeatList));
	}
}
