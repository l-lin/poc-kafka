package lin.louis.poc.hbv.config;

public class TopicsProperties {

	private String from;

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

		private Topic valid;

		private Topic invalid;

		public Topic getValid() {
			return valid;
		}

		public void setValid(Topic valid) {
			this.valid = valid;
		}

		public Topic getInvalid() {
			return invalid;
		}

		public void setInvalid(Topic invalid) {
			this.invalid = invalid;
		}
	}

	public static class Topic {

		private String name;

		private int partitions;

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
