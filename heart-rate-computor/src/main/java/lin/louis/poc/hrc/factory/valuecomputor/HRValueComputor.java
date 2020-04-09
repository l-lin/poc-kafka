package lin.louis.poc.hrc.factory.valuecomputor;

import java.util.Arrays;


/**
 * Computes the heart rate value from a given heart beats
 */
public class HRValueComputor {
	public double compute(int... values) {
		if (values == null || values.length == 0) {
			return 0d;
		}
		Arrays.sort(values);
		if (values.length % 2 == 0) {
			return ((double) values[values.length / 2] + (double) values[values.length / 2 - 1]) / 2;
		}
		return values[values.length / 2];
	}
}
