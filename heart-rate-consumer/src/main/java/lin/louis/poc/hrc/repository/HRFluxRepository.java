package lin.louis.poc.hrc.repository;

import lin.louis.poc.models.HeartRate;
import reactor.core.publisher.Flux;


public interface HRFluxRepository {
	Flux<HeartRate> read(long userId);
}
