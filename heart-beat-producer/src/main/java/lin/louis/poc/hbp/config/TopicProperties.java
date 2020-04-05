package lin.louis.poc.hbp.config;

public class TopicProperties {

	/**
	 * Kafka topic name to send to
	 */
	private String name;

	/**
	 * Number of partitions for this topic
	 */
	private int partitions;

	/**
	 * Number of replicas for this topic
	 */
	private int replicas;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPartitions() {
		return partitions;
	}

	public void setPartitions(int partitions) {
		this.partitions = partitions;
	}

	public int getReplicas() {
		return replicas;
	}

	public void setReplicas(int replicas) {
		this.replicas = replicas;
	}
}
