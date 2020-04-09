package lin.louis.poc.hrc.repository;

import lin.louis.poc.models.HeartRate;
import reactor.core.publisher.Flux;


public interface HRRepository {
	Flux<HeartRate> read(long userId);
}
