package lin.louis.poc.hrc.web;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lin.louis.poc.hrc.dto.HeartRateDTO;
import lin.louis.poc.hrc.service.HRFetcher;


@RestController
public class HRController {

	private final HRFetcher hrFetcher;

	public HRController(HRFetcher hrFetcher) {
		this.hrFetcher = hrFetcher;
	}

	@GetMapping(path = "/users/{userId}/heart-rates/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Publisher<HeartRateDTO> heartRateFlux(@PathVariable long userId) {
		return hrFetcher.fetch(userId);
	}
}
