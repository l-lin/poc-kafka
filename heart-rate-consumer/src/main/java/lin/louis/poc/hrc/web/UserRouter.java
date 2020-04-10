package lin.louis.poc.hrc.web;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import lin.louis.poc.hrc.repository.HRRepository;
import reactor.core.publisher.Mono;


@Component
public class UserRouter {

	@Bean
	RouterFunction<ServerResponse> userRoute(HRRepository hrRepository) {
		return RouterFunctions.route(
				GET("/users/ids").and(accept(MediaType.APPLICATION_JSON)),
				request -> usersHandler(hrRepository)
		);
	}

	private Mono<ServerResponse> usersHandler(HRRepository hrRepository) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
							 .body(BodyInserters.fromValue(hrRepository.findUserIds()));
	}
}
