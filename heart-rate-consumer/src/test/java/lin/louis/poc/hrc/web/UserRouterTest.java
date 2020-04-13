package lin.louis.poc.hrc.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

import lin.louis.poc.hrc.dto.UserDTO;
import lin.louis.poc.hrc.repository.HRRepository;


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = UserRouter.class)
class UserRouterTest {

	@MockBean
	HRRepository hrRepository;

	@Autowired
	WebTestClient webTestClient;

	@Test
	void userRoute() {
		// GIVEN
		var userIds = Arrays.asList(100L, 200L, 300L);
		Mockito.when(hrRepository.findUserIds()).thenReturn(userIds);

		// WHEN
		var users = webTestClient.get().uri("/users")
								 .accept(MediaType.APPLICATION_JSON)
								 .exchange()
								 .expectStatus().isOk()
								 .returnResult(UserDTO.class)
								 .getResponseBody()
								 .take(userIds.size())
								 .collectList()
								 .block();

		// THEN
		assertNotNull(users);
		assertEquals(userIds.size(), users.size());
		for (int i = 0; i < userIds.size(); i++) {
			assertEquals(userIds.get(i), users.get(i).getUserId());
		}
	}
}
