package lin.louis.poc.hrc.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lin.louis.poc.models.HeartBeat;
import lin.louis.poc.models.HeartBeatQRS;


class HBInputBuilderTest {

	private static final long USER_ID = 123;

	private static final int NB_HEART_BEATS = 8;

	@Test
	void build_withNotEnoughHeartBeats() {
		// GIVEN
		var allHeartBeats = buildHeartBeats(4);
		var offset = allHeartBeats.get(0).getTimestamp();

		// WHEN
		var heartBeats = HBInputBuilder.from(allHeartBeats)
									   .withNbHeartBeats(8)
									   .withOffset(offset)
									   .build();

		// THEN
		assertNotNull(heartBeats);
		assertEquals(4, heartBeats.size());
		for (var i = 0; i < allHeartBeats.size(); i++) {
			assertEquals(allHeartBeats.get(i), heartBeats.get(i));
		}
	}

	@Test
	void build_withOffsetAtOneButLast() {
		// GIVEN
		var allHeartBeats = buildHeartBeats(10);
		var offset = allHeartBeats.get(allHeartBeats.size() - 2).getTimestamp();

		// WHEN
		var heartBeats = HBInputBuilder.from(allHeartBeats)
									   .withNbHeartBeats(8)
									   .withOffset(offset)
									   .build();

		// THEN
		assertNotNull(heartBeats);
		assertEquals(NB_HEART_BEATS, heartBeats.size());
		var start = heartBeats.get(0).getHri();
		assertEquals(2, start);
		for (var i = start; i < allHeartBeats.size(); i++) {
			assertEquals(allHeartBeats.get(i), heartBeats.get(i - start));
		}
	}

	@Test
	void build_withOffsetAtLast() {
		// GIVEN
		var allHeartBeats = buildHeartBeats(10);
		var offset = allHeartBeats.get(allHeartBeats.size() - 1).getTimestamp();

		// WHEN
		var heartBeats = HBInputBuilder.from(allHeartBeats)
									   .withNbHeartBeats(8)
									   .withOffset(offset)
									   .build();

		// THEN
		assertNotNull(heartBeats);
		assertEquals(NB_HEART_BEATS, heartBeats.size());
		var start = heartBeats.get(0).getHri();
		assertEquals(2, start);
		for (var i = start; i < allHeartBeats.size(); i++) {
			assertEquals(allHeartBeats.get(i), heartBeats.get(i - start));
		}
	}

	@Test
	void build_withOffsetInMiddle() {
		// GIVEN
		var allHeartBeats = buildHeartBeats(20);
		var offset = allHeartBeats.get(9).getTimestamp();

		// WHEN
		var heartBeats = HBInputBuilder.from(allHeartBeats)
									   .withNbHeartBeats(8)
									   .withOffset(offset)
									   .build();

		// THEN
		assertNotNull(heartBeats);
		assertEquals(17, heartBeats.size());
		var start = heartBeats.get(0).getHri();
		assertEquals(3, start);
		for (var i = start; i < allHeartBeats.size(); i++) {
			assertEquals(allHeartBeats.get(i), heartBeats.get(i - start));
		}
	}

	@Test
	void build_withOffsetGreaterThenEveryone_shouldReturnLastNHeartBeats() {
		// GIVEN
		var allHeartBeats = buildHeartBeats(10);
		var offset = LocalDateTime.of(2020, Month.APRIL, 12, 17, 47, 0).toInstant(ZoneOffset.UTC);

		// WHEN
		var heartBeats = HBInputBuilder.from(allHeartBeats)
									   .withNbHeartBeats(8)
									   .withOffset(offset)
									   .build();

		// THEN
		assertNotNull(heartBeats);
		assertEquals(NB_HEART_BEATS, heartBeats.size());
		var start = heartBeats.get(0).getHri();
		assertEquals(2, start);
		for (var i = start; i < allHeartBeats.size(); i++) {
			assertEquals(allHeartBeats.get(i), heartBeats.get(i - start));
		}
	}

	private List<HeartBeat> buildHeartBeats(int nb) {
		var allHeartBeats = new ArrayList<HeartBeat>();
		// using heart beat value to check
		for (int i = 0; i < nb; i++) {
			allHeartBeats.add(
					new HeartBeat(
							USER_ID,
							i,
							HeartBeatQRS.A,
							LocalDateTime.of(2020, Month.APRIL, 11, 17, 47, i).toInstant(ZoneOffset.UTC)
					)
			);
		}
		return allHeartBeats;
	}
}
