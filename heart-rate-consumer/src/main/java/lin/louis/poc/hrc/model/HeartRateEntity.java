package lin.louis.poc.hrc.model;

import java.time.Instant;
import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
// the table name must be escaped because it's not a standard name in Postgres world to have a hyphen in the table name
// which is auto-generated by heart-rate-computor (i.e. kafka connect)
@Table(name = "\"heart-rates\"")
public class HeartRateEntity {

	@Id
	@EmbeddedId
	private HeartRateId heartRateId;

	private long userId;

	private double value;

	private Instant timestamp;

	private boolean isReset;

	public HeartRateId getHeartRateId() {
		return heartRateId;
	}

	public void setHeartRateId(HeartRateId heartRateId) {
		this.heartRateId = heartRateId;
	}

	public boolean isReset() {
		return isReset;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public HeartRateEntity() {
	}

	public HeartRateEntity(HeartRateId heartRateId, long userId, double value, Instant timestamp, boolean isReset) {
		this.heartRateId = heartRateId;
		this.userId = userId;
		this.value = value;
		this.timestamp = timestamp;
		this.isReset = isReset;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		HeartRateEntity that = (HeartRateEntity) o;
		return userId == that.userId &&
				Double.compare(that.value, value) == 0 &&
				isReset == that.isReset &&
				heartRateId.equals(that.heartRateId) &&
				timestamp.equals(that.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(heartRateId, userId, value, timestamp, isReset);
	}
}
