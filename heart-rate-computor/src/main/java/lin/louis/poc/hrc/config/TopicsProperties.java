package lin.louis.poc.hrc.config;

public class TopicsProperties {
	/**
	 * Topic name to read from
	 */
	private String from;
	/**
	 * Topic to send message to
	 */
	private To to = new To();

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public To getTo() {
		return to;
	}

	public void setTo(To to) {
		this.to = to;
	}

	public static class To {
		/**
		 * Topic name to send message
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
}
