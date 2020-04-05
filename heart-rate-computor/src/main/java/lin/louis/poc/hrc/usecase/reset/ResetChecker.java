package lin.louis.poc.hrc.usecase.reset;

import java.util.Queue;

import lin.louis.poc.models.HeartBeat;


public interface ResetChecker {

	boolean isReset(Queue<HeartBeat> heartBeatQueue);
}
