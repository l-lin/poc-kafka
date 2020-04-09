package lin.louis.poc.hrc.dto;

import java.time.Instant;


/**
 * Not using the {@link lin.louis.poc.models.HeartRate} because it does not do well with Jackson and generate multiple
 * errors, like this getter {@link lin.louis.poc.models.HeartRate#getSchema()}, hence the existence of this DTO.
 */
public class HeartRateDTO {

	private long userId;

	private double value;

	private Instant timestamp;

	private boolean isReset;

	public HeartRateDTO() {
	}

	public HeartRateDTO(long userId, double value, Instant timestamp, boolean isReset) {
		this.userId = userId;
		this.value = value;
		this.timestamp = timestamp;
		this.isReset = isReset;
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

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isReset() {
		return isReset;
	}

	public void setReset(boolean reset) {
		isReset = reset;
	}
}
