package lin.louis.poc.hrc.factory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lin.louis.poc.models.HeartBeat;


/**
 * Builder to help create a list of heart beats as input to compute heart rates
 */
public class HBInputBuilder {

	private final List<HeartBeat> allHeartBeats;

	private int nbHeartBeats;

	private Instant offset;

	private HBInputBuilder(List<HeartBeat> allHeartBeats) {
		this.allHeartBeats = allHeartBeats;
	}

	public static HBInputBuilder from(List<HeartBeat> allHeartBeats) {
		var list = new ArrayList<>(allHeartBeats);
		list.sort(Comparator.comparing(HeartBeat::getTimestamp));
		return new HBInputBuilder(list);
	}

	public HBInputBuilder withNbHeartBeats(int nbHeartBeats) {
		this.nbHeartBeats = nbHeartBeats;
		return this;
	}

	public HBInputBuilder withOffset(Instant offset) {
		this.offset = offset;
		return this;
	}

	public List<HeartBeat> build() {
		if (!hasEnoughHeartBeats(allHeartBeats.size())) {
			return allHeartBeats;
		}

		int startIndex = computeStartIndex();
		if (!hasEnoughHeartBeats(allHeartBeats.size() - startIndex)) {
			// get last nbHeartBeats from allHeartBeats
			startIndex = allHeartBeats.size() - nbHeartBeats;
		}
		return allHeartBeats.subList(startIndex, allHeartBeats.size());
	}

	private boolean hasEnoughHeartBeats(int nb) {
		return nb >= nbHeartBeats;
	}

	private int computeStartIndex() {
		var startIndex = 0;
		var indexOffset = findIndexOffset(allHeartBeats);
		if (indexOffset >= nbHeartBeats - 1) {
			var nextHBToCompute = indexOffset + 1;
			startIndex = nextHBToCompute - nbHeartBeats + 1;
		}
		return startIndex;
	}

	private int findIndexOffset(List<HeartBeat> heartBeats) {
		for (int i = 0; i < heartBeats.size(); i++) {
			var heartBeat = heartBeats.get(i);
			if (offset.isBefore(heartBeat.getTimestamp())
					|| offset.equals(heartBeat.getTimestamp())) {
				return i;
			}
		}
		return heartBeats.size() - 1;
	}
}
