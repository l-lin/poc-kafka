package lin.louis.poc.hrc.usecase;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


class HRValueComputorTest {

	private HRValueComputor computor = new HRValueComputor();

	@Test
	void compute() {
		assertAll(
				() -> {
					assertEquals(4.5, computor.compute(1, 2, 3, 4, 5, 6, 7, 8), "pair number of values");
					assertEquals(4, computor.compute(1, 2, 3, 4, 5, 6, 7), "impair number of values");
					assertEquals(81.5, computor.compute(80, 100, 83, 80, 91, 88, 70, 10), "impair number of values");
					assertEquals(85.5, computor.compute(100, 83, 80, 91, 88, 70, 10, 90), "impair number of values");
					assertEquals(85.5, computor.compute(83, 80, 91, 88, 70, 10, 90, 201), "impair number of values");
					assertEquals(88, computor.compute(80, 91, 88, 70, 10, 90, 201, 88), "impair number of values");
					assertEquals(89, computor.compute(91, 88, 70, 10, 90, 201, 88, 222), "impair number of values");
					assertEquals(88.5, computor.compute(88, 70, 10, 90, 201, 88, 222, 89), "impair number of values");
					assertEquals(89.5, computor.compute(70, 10, 90, 201, 88, 222, 89, 100), "impair number of values");
					assertEquals(95, computor.compute(10, 90, 201, 88, 222, 89, 100, 101), "impair number of values");
					assertEquals(5, computor.compute(5), "one element");
					assertEquals(0, computor.compute(), "no element");
				}
		);
	}
}