package lin.louis.poc.hrc.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import lin.louis.poc.hrc.dto.HeartRateDTO;
import lin.louis.poc.hrc.model.HeartRateEntity;
import lin.louis.poc.hrc.repository.HRRepository;


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = HRRouter.class)
class HRRouterTest {

	private static final long USER_ID = 123L;

	@MockBean
	HRRepository hrRepository;

	@Autowired
	WebTestClient webTestClient;

	@Test
	void hrRoute() {
		// GIVEN
		var entities = Arrays.asList(
				new HeartRateEntity(null, USER_ID, 90d, Instant.now(), false),
				new HeartRateEntity(null, USER_ID, 91d, Instant.now(), false),
				new HeartRateEntity(null, USER_ID, Double.NaN, Instant.now(), false)
		);
		Mockito.when(hrRepository.findByUserIdAndTimestampIsAfterOrderByTimestampDesc(
				Mockito.anyLong(),
				Mockito.any(Instant.class)
		)).thenReturn(entities);

		// WHEN
		var heartRates = webTestClient.get().uri("/users/" + USER_ID + "/heart-rates")
									  .accept(MediaType.APPLICATION_JSON)
									  .exchange()
									  .expectStatus().isOk()
									  .returnResult(HeartRateDTO.class)
									  .getResponseBody()
									  .take(entities.size())
									  .collectList()
									  .block();

		// THEN
		assertNotNull(heartRates);
		assertEquals(entities.size(), heartRates.size());
		for (int i = 0; i < entities.size(); i++) {
			assertEquals(entities.get(i).getUserId(), heartRates.get(i).getUserId());
			assertEquals(entities.get(i).getValue(), heartRates.get(i).getValue());
			assertEquals(entities.get(i).getTimestamp(), heartRates.get(i).getTimestamp());
			assertEquals(entities.get(i).isReset(), heartRates.get(i).isReset());
		}
	}
}
