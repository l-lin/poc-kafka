package lin.louis.poc.hrc.web;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lin.louis.poc.hrc.dto.HeartRateDTO;
import lin.louis.poc.hrc.repository.HRRepository;
import reactor.core.publisher.Mono;


@Component
public class HRRouter {

	@Bean
	RouterFunction<ServerResponse> hrRoute(HRRepository hrRepository) {
		return route(
				GET("/heart-rates/{userId}").and(accept(MediaType.APPLICATION_JSON)),
				request -> heartRateHandler(request, hrRepository)
		);
	}

	private Mono<ServerResponse> heartRateHandler(ServerRequest request, HRRepository hrRepository) {
		var userId = Long.parseLong(request.pathVariable("userId"));
		var seconds = Integer.parseInt(request.queryParam("lastNSeconds").orElse("60"));
		var lastNSeconds = Instant.now().minus(seconds, ChronoUnit.SECONDS);

		var hearRates = hrRepository.findByUserIdAndTimestampIsAfter(userId, lastNSeconds);
		var hearRatesDTO = hearRates.stream()
									.map(hr -> new HeartRateDTO(
											hr.getUserId(),
											hr.getValue(),
											hr.getTimestamp(),
											hr.isReset()
									))
									.collect(Collectors.toList());
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
							 .body(BodyInserters.fromValue(hearRatesDTO));
	}
}
