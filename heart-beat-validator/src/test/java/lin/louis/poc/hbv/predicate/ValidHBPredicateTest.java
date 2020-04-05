package lin.louis.poc.hbv.predicate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.apache.kafka.streams.kstream.Predicate;
import org.junit.jupiter.api.Test;

import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


class ValidHBPredicateTest {

	private static final long USER_ID = 123L;

	private Predicate<Long, HeartBeat> predicate = new ValidHBPredicate();

	@Test
	void test_shouldValidate_whenHeartBeatHasValidValues() {
		assertAll("should validate when heart beat has valid values", () -> {
			assertTrue(predicate.test(USER_ID, new HeartBeat(USER_ID, 100, HeartBeatQRS.A, Instant.now())));
			assertTrue(predicate.test(USER_ID, new HeartBeat(USER_ID, 0, HeartBeatQRS.A, Instant.now())));
			assertTrue(predicate.test(USER_ID, new HeartBeat(USER_ID, 250, HeartBeatQRS.A, Instant.now())));
		});
		assertAll("should not validate when heart beat has bad values", () -> {
			assertFalse(predicate.test(-1L, new HeartBeat(-1L, 100, HeartBeatQRS.X, Instant.now())));
			assertFalse(predicate.test(USER_ID, null));
		});
	}
}
