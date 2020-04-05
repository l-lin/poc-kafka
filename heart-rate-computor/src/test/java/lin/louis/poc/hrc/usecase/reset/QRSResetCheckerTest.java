package lin.louis.poc.hrc.usecase.reset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.junit.jupiter.api.Test;

import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


class QRSResetCheckerTest {

	private static final long USER_ID = 123L;

	private ResetChecker checker = new QRSResetChecker();

	@Test
	void isReset() {
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.N, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 8, HeartBeatQRS.F, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 88, HeartBeatQRS.X, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 180, HeartBeatQRS.P, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 8, HeartBeatQRS.A, Instant.now()));
		assertTrue(checker.isReset(heartBeats), "Invalid heart beat");

		heartBeats.clear();
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.N, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 8, HeartBeatQRS.F, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 88, null, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 180, HeartBeatQRS.P, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 8, HeartBeatQRS.A, Instant.now()));
		assertTrue(checker.isReset(heartBeats), "Null heart beat");
	}

	@Test
	void isNotReset() {
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.N, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 8, HeartBeatQRS.F, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 88, HeartBeatQRS.P, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 180, HeartBeatQRS.P, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 8, HeartBeatQRS.A, Instant.now()));
		assertFalse(checker.isReset(heartBeats), "Happy path");
	}
}