package lin.louis.poc.hrc;

import java.time.Instant;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

import lin.louis.poc.models.HeartBeat;


public class HeartBeatTimestampExtractor implements TimestampExtractor {

	@Override
	public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
		long timestamp = -1;
		final HeartBeat heartBeat = (HeartBeat) record.value();
		if (heartBeat != null) {
			timestamp = heartBeat.getTimestamp().toEpochMilli();
		}
		if (timestamp < 0) {
			// Invalid timestamp!  Attempt to estimate a new timestamp,
			// otherwise fall back to wall-clock time (processing-time).
			if (previousTimestamp >= 0) {
				return previousTimestamp;
			}
			return Instant.now().toEpochMilli();
		}
		return timestamp;
	}
}
