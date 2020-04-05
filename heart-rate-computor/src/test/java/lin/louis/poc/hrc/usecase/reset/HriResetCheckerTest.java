package lin.louis.poc.hrc.usecase.reset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.junit.jupiter.api.Test;

import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


class HriResetCheckerTest {

	private static final long USER_ID = 123L;

	private ResetChecker checker = new HriResetChecker(0, 250);

	@Test
	void isReset() {
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(USER_ID, -2, HeartBeatQRS.N, Instant.now()));
		assertTrue(checker.isReset(heartBeats), "Heart beat below min");

		heartBeats.clear();
		heartBeats.add(new HeartBeat(USER_ID, 300, HeartBeatQRS.N, Instant.now()));
		assertTrue(checker.isReset(heartBeats), "Heart beat above max");
	}

	@Test
	void isNotReset() {
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.N, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 8, HeartBeatQRS.N, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 88, HeartBeatQRS.N, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 180, HeartBeatQRS.N, Instant.now()));
		heartBeats.add(new HeartBeat(USER_ID, 8, HeartBeatQRS.N, Instant.now()));
		assertFalse(checker.isReset(heartBeats), "Happy path");

		heartBeats.clear();
		heartBeats.add(new HeartBeat(USER_ID, 0, HeartBeatQRS.N, Instant.now()));
		assertFalse(checker.isReset(heartBeats), "Min value");

		heartBeats.clear();
		heartBeats.add(new HeartBeat(USER_ID, 250, HeartBeatQRS.N, Instant.now()));
		assertFalse(checker.isReset(heartBeats), "Max value");
	}
}