package lin.louis.poc.hrc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lin.louis.poc.hrc.dto.HeartRateDTO;
import lin.louis.poc.hrc.repository.HRRepository;
import reactor.core.publisher.Flux;


public class HRFetcher {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final HRRepository hrRepository;

	public HRFetcher(HRRepository hrRepository) {this.hrRepository = hrRepository;}

	public Flux<HeartRateDTO> fetch(long userId) {
		logger.info("Reading heart rates in stream for user {}", userId);
		return hrRepository.read(userId)
						   .map(hr -> new HeartRateDTO(hr.getUserId(),
													   hr.getValue(),
													   hr.getTimestamp(),
													   hr.getIsReset()
						   ));
	}
}
