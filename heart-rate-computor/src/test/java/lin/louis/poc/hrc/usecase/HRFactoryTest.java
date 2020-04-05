package lin.louis.poc.hrc.usecase;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.junit.jupiter.api.Test;

import lin.louis.poc.hrc.usecase.reset.GapResetChecker;
import lin.louis.poc.hrc.usecase.reset.HriResetChecker;
import lin.louis.poc.hrc.usecase.reset.QRSResetChecker;
import lin.louis.poc.hrc.usecase.reset.ResetCheckerFacade;
import lin.louis.poc.hrc.usecase.reset.TimestampResetChecker;
import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


class HRFactoryTest {

	private static final long USER_ID = 123L;

	private HRFactory hrFactory = new HRFactory(
			8,
			new ResetCheckerFacade(Arrays.asList(
					new GapResetChecker(Duration.ofSeconds(5)),
					new HriResetChecker(0, 250),
					new QRSResetChecker(),
					new TimestampResetChecker()
			)),
			new HRValueComputor()
	);

	@Test
	void create() {
		// GIVEN
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.N, newInstant(1)));
		heartBeats.add(new HeartBeat(USER_ID, 100, HeartBeatQRS.V, newInstant(2)));
		heartBeats.add(new HeartBeat(USER_ID, 83, HeartBeatQRS.N, newInstant(3)));
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.P, newInstant(4)));
		heartBeats.add(new HeartBeat(USER_ID, 91, HeartBeatQRS.A, newInstant(5)));
		heartBeats.add(new HeartBeat(USER_ID, 88, HeartBeatQRS.N, newInstant(7)));
		heartBeats.add(new HeartBeat(USER_ID, 70, HeartBeatQRS.N, newInstant(8)));
		heartBeats.add(new HeartBeat(USER_ID, 10, HeartBeatQRS.F, newInstant(10)));

		// WHEN
		var heartRate = hrFactory.create(USER_ID, heartBeats);

		// THEN
		assertNotNull(heartRate);
		assertAll(
				() -> {
					assertEquals(newInstant(10), heartRate.getTimestamp());
				},
				() -> assertEquals(81.5d, heartRate.getValue())
		);
	}

	@Test
	void create_moreThan8HeartBeats() {
		// GIVEN
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.N, newInstant(1)));
		heartBeats.add(new HeartBeat(USER_ID, 100, HeartBeatQRS.V, newInstant(2)));
		heartBeats.add(new HeartBeat(USER_ID, 83, HeartBeatQRS.N, newInstant(3)));
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.P, newInstant(4)));
		heartBeats.add(new HeartBeat(USER_ID, 91, HeartBeatQRS.A, newInstant(5)));
		heartBeats.add(new HeartBeat(USER_ID, 88, HeartBeatQRS.N, newInstant(7)));
		heartBeats.add(new HeartBeat(USER_ID, 70, HeartBeatQRS.N, newInstant(8)));
		heartBeats.add(new HeartBeat(USER_ID, 10, HeartBeatQRS.F, newInstant(10)));
		heartBeats.add(new HeartBeat(USER_ID, 110, HeartBeatQRS.N, newInstant(11)));
		heartBeats.add(new HeartBeat(USER_ID, 10, HeartBeatQRS.F, newInstant(13)));
		heartBeats.add(new HeartBeat(USER_ID, 193, HeartBeatQRS.V, newInstant(17)));

		// WHEN
		var heartRate = hrFactory.create(USER_ID, heartBeats);

		// THEN
		assertNotNull(heartRate);
		assertAll(
				() -> {
					assertNotNull(heartRate.getTimestamp());
					assertEquals(newInstant(17), heartRate.getTimestamp());
				},
				() -> assertEquals(84.0d, heartRate.getValue())
		);
	}

	@Test
	void create_reset() {
		// GIVEN
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.N, newInstant(1)));
		heartBeats.add(new HeartBeat(USER_ID, 100, HeartBeatQRS.V, newInstant(2)));
		heartBeats.add(new HeartBeat(USER_ID, 83, HeartBeatQRS.N, newInstant(3)));
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.P, newInstant(4)));
		// Reset at this moment
		heartBeats.add(new HeartBeat(USER_ID, 91, HeartBeatQRS.X, newInstant(5)));
		heartBeats.add(new HeartBeat(USER_ID, 88, HeartBeatQRS.N, newInstant(7)));
		heartBeats.add(new HeartBeat(USER_ID, 70, HeartBeatQRS.N, newInstant(8)));
		heartBeats.add(new HeartBeat(USER_ID, 10, HeartBeatQRS.F, newInstant(10)));
		heartBeats.add(new HeartBeat(USER_ID, 110, HeartBeatQRS.N, newInstant(11)));
		heartBeats.add(new HeartBeat(USER_ID, 10, HeartBeatQRS.F, newInstant(13)));
		heartBeats.add(new HeartBeat(USER_ID, 193, HeartBeatQRS.V, newInstant(17)));

		// WHEN
		var heartRate = hrFactory.create(USER_ID, heartBeats);

		// THEN
		assertNotNull(heartRate);
		assertAll(
				() -> {
					assertNotNull(heartRate.getTimestamp());
					assertEquals(newInstant(17), heartRate.getTimestamp());
				},
				() -> {
					assertEquals(Double.NaN, heartRate.getValue());
					assertTrue(heartRate.getIsReset());
				}
		);
	}

	@Test
	void create_lessThan8HeartBeats() {
		// GIVEN
		var heartBeats = new CircularFifoQueue<HeartBeat>(8);
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.N, newInstant(1)));
		heartBeats.add(new HeartBeat(USER_ID, 100, HeartBeatQRS.V, newInstant(2)));
		heartBeats.add(new HeartBeat(USER_ID, 83, HeartBeatQRS.N, newInstant(3)));
		heartBeats.add(new HeartBeat(USER_ID, 80, HeartBeatQRS.P, newInstant(4)));

		// WHEN
		var heartRate = hrFactory.create(USER_ID, heartBeats);

		// THEN
		assertNotNull(heartRate);
		assertAll(
				() -> {
					assertNotNull(heartRate.getTimestamp());
					assertEquals(newInstant(4), heartRate.getTimestamp());
				},
				() -> assertEquals(Double.NaN, heartRate.getValue())
		);
	}

	@Test
	void create_emptyList() {
		// WHEN
		var heartRate = hrFactory.create(USER_ID, new CircularFifoQueue<>(8));

		// THEN
		assertNotNull(heartRate);
		assertAll(
				() -> assertEquals(Double.NaN, heartRate.getValue())
		);
	}

	private Instant newInstant(int seconds) {
		return LocalDateTime.of(2019, Month.NOVEMBER.getValue(), 25, 10, 0, seconds).toInstant(ZoneOffset.UTC);
	}
}