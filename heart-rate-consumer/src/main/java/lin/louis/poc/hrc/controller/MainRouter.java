package lin.louis.poc.hrc.controller;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class MainRouter {

	/**
	 * Redirect / to index.html since spring webflux does not handle natively
	 * @see <a href="https://github.com/spring-projects/spring-boot/issues/9785">Github issue for more info</a>
	 */
	@Bean
	public RouterFunction<ServerResponse> index(@Value("classpath:/static/index.html") Resource indexHtml) {
		return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml));
	}
}
