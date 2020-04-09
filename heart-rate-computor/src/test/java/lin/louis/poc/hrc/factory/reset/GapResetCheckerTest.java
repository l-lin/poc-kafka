package lin.louis.poc.hrc.factory.reset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.junit.jupiter.api.Test;

import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


class GapResetCheckerTest {

	private static final long USER_ID = 123L;

	private final ResetChecker checker = new GapResetChecker(Duration.ofSeconds(5));

	@Test
	void isReset() {
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(
				USER_ID,
				80,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 11).toInstant(
						ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				0,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 12).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				88,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 21).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				180,
				HeartBeatQRS.P,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 23).toInstant(ZoneOffset.UTC)
		));
		assertTrue(checker.isReset(heartBeats), "With a gap");
	}

	@Test
	void isNotReset() {
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(
				USER_ID,
				80,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 11).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				0,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 12).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				0,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 14).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				88,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 18).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				180,
				HeartBeatQRS.P,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 19).toInstant(ZoneOffset.UTC)
		));
		assertFalse(checker.isReset(heartBeats));

		heartBeats.clear();
		heartBeats.add(new HeartBeat(
				USER_ID,
				80,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 19).toInstant(ZoneOffset.UTC)
		));
		assertFalse(checker.isReset(heartBeats), "One heart beat");
	}
}