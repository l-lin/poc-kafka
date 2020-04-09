package lin.louis.poc.hrc.factory.reset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.junit.jupiter.api.Test;

import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


class ResetCheckerFacadeTest {

	private static final long USER_ID = 123L;

	private final ResetCheckerFacade checkerFacade = new ResetCheckerFacade(Arrays.asList(
			new GapResetChecker(Duration.ofSeconds(5)),
			new HriResetChecker(0, 250),
			new QRSResetChecker(),
			new TimestampResetChecker()
	));

	@Test
	void isReset() {
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(
				USER_ID,
				80,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 21).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				0,
				HeartBeatQRS.X,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 23).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				-88,
				HeartBeatQRS.P,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 27).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				180,
				HeartBeatQRS.P,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 29).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				8,
				HeartBeatQRS.A,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 31).toInstant(ZoneOffset.UTC)
		));
		assertTrue(checkerFacade.isReset(heartBeats));
	}

	@Test
	void isNotReset() {
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(
				USER_ID,
				80,
				HeartBeatQRS.N,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 21).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				0,
				HeartBeatQRS.F,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 23).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				88,
				HeartBeatQRS.P,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 27).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				180,
				HeartBeatQRS.P,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 29).toInstant(ZoneOffset.UTC)
		));
		heartBeats.add(new HeartBeat(
				USER_ID,
				8,
				HeartBeatQRS.A,
				LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, 31).toInstant(ZoneOffset.UTC)
		));
		assertFalse(checkerFacade.isReset(heartBeats));
	}
}