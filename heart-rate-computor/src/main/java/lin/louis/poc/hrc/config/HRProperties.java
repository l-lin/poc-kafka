package lin.louis.poc.hrc.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.convert.DurationUnit;


public class HRProperties {
	/**
	 * Duration between two consecutive heart beats to be considered as a Gap
	 */
	@DurationUnit(ChronoUnit.SECONDS)
	private Duration gapDuration;
	/**
	 * Number of heart beats to use for computing a heart rate signal
	 */
	private int nbHeartBeats;
	private HriProperties hri = new HriProperties();

	public Duration getGapDuration() {
		return gapDuration;
	}

	public void setGapDuration(Duration gapDuration) {
		this.gapDuration = gapDuration;
	}

	public int getNbHeartBeats() {
		return nbHeartBeats;
	}

	public void setNbHeartBeats(int nbHeartBeats) {
		this.nbHeartBeats = nbHeartBeats;
	}

	public HriProperties getHri() {
		return hri;
	}

	public void setHri(HriProperties hri) {
		this.hri = hri;
	}

	public class HriProperties {
		/**
		 * Min HRI (Instant Heart Rate) value a heart beat must have
		 */
		private int min;
		/**
		 * Max HRI (Instant Heart Rate) value a heart beat must have
		 */
		private int max;

		public int getMin() {
			return min;
		}

		public void setMin(int min) {
			this.min = min;
		}

		public int getMax() {
			return max;
		}

		public void setMax(int max) {
			this.max = max;
		}
	}
}
