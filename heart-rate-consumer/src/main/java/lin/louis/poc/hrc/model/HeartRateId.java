package lin.louis.poc.hrc.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;


/**
 * Object representing the 3 columns used as primary key for the table "heart-rates" in the database. They are
 * auto-generated from kafka connect sink.
 */
public class HeartRateId implements Serializable {
	@Column(name = "__connect_topic")
	private String connectTopic;

	@Column(name = "__connect_partition")
	private int connectPartition;

	@Column(name = "__connect_offset")
	private long connectOffset;

	public String getConnectTopic() {
		return connectTopic;
	}

	public void setConnectTopic(String connectTopic) {
		this.connectTopic = connectTopic;
	}

	public int getConnectPartition() {
		return connectPartition;
	}

	public void setConnectPartition(int connectPartition) {
		this.connectPartition = connectPartition;
	}

	public long getConnectOffset() {
		return connectOffset;
	}

	public void setConnectOffset(long connectOffset) {
		this.connectOffset = connectOffset;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		HeartRateId that = (HeartRateId) o;
		return connectPartition == that.connectPartition &&
				connectOffset == that.connectOffset &&
				connectTopic.equals(that.connectTopic);
	}

	@Override
	public int hashCode() {
		return Objects.hash(connectTopic, connectPartition, connectOffset);
	}
}
