package lin.louis.poc.hrc.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;

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
import lin.louis.poc.hrc.service.HRFetcher;
import reactor.core.publisher.Flux;


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = HRController.class)
class HRControllerTest {

	private static final long USER_ID = 123L;

	@MockBean
	private HRFetcher hrFetcher;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void heartRateFlux() {
		// GIVEN
		var heartRates = new HeartRateDTO[] {
				new HeartRateDTO(USER_ID, 90d, Instant.now(), false),
				new HeartRateDTO(USER_ID, 91d, Instant.now(), false),
				new HeartRateDTO(USER_ID, Double.NaN, Instant.now(), false)
		};
		Mockito.when(hrFetcher.fetch(USER_ID)).thenReturn(Flux.fromArray(heartRates));

		// WHEN
		var heartRateList = webTestClient.get().uri("/users/{userId}/heart-rates/stream", USER_ID)
										 .accept(MediaType.TEXT_EVENT_STREAM)
										 .exchange()
										 .expectStatus().isOk()
										 .returnResult(HeartRateDTO.class)
										 .getResponseBody()
										 .take(heartRates.length)
										 .collectList()
										 .block();

		// THEN
		assertNotNull(heartRateList);
		assertEquals(heartRates.length, heartRateList.size());
		for (int i = 0; i < heartRates.length; i++) {
			assertEquals(heartRates[i].getUserId(), heartRateList.get(i).getUserId());
			assertEquals(heartRates[i].getValue(), heartRateList.get(i).getValue());
			assertEquals(heartRates[i].getTimestamp(), heartRateList.get(i).getTimestamp());
			assertEquals(heartRates[i].isReset(), heartRateList.get(i).isReset());
		}
	}
}
