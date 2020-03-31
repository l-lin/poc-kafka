package lin.louis.poc.hbv.config;

public class HeartBeatProperties {

	private Hri hri = new Hri();

	public Hri getHri() {
		return hri;
	}

	public void setHri(Hri hri) {
		this.hri = hri;
	}

	public static class Hri {

		private int min;

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
