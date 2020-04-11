package lin.louis.poc.hrc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lin.louis.poc.hrc.dto.HeartRateDTO;
import lin.louis.poc.hrc.repository.HRFluxRepository;
import reactor.core.publisher.Flux;


public class HRFetcher {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final HRFluxRepository hrFluxRepository;

	public HRFetcher(HRFluxRepository hrFluxRepository) {this.hrFluxRepository = hrFluxRepository;}

	public Flux<HeartRateDTO> fetch(long userId) {
		logger.debug("Reading heart rates in stream for user {}", userId);
		return hrFluxRepository.read(userId)
							   .map(hr -> new HeartRateDTO(hr.getUserId(),
													   hr.getValue(),
													   hr.getTimestamp(),
													   hr.getIsReset()
						   ));
	}
}
