package lin.louis.poc.hrc.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import lin.louis.poc.hrc.model.HeartRateEntity;


public interface HRRepository extends CrudRepository<HeartRateEntity, Void> {

	List<HeartRateEntity> findByUserIdAndTimestampIsAfterOrderByTimestampDesc(long userId, Instant timestampRef);

	@Query(value = "SELECT DISTINCT user_id FROM \"heart-rates\" ORDER BY user_id", nativeQuery = true)
	List<Long> findUserIds();
}
