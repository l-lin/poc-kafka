package lin.louis.poc.hrc.repository.kafka;

import java.util.Collections;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import lin.louis.poc.hrc.repository.HRRepository;
import lin.louis.poc.models.HeartRate;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;


public class HRKafkaConsumer implements HRRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final KafkaProperties kafkaProperties;

	public HRKafkaConsumer(KafkaProperties kafkaProperties) {this.kafkaProperties = kafkaProperties;}

	@Override
	public Flux<HeartRate> read(long userId) {
		var consumerProperties = kafkaProperties.buildConsumerProperties();
		consumerProperties.put(
				ConsumerConfig.GROUP_ID_CONFIG,
				// Set a different Kafka group so the consumers are independent and can all read
				consumerProperties.get(ConsumerConfig.GROUP_ID_CONFIG) + "-" + UUID.randomUUID().toString()
		);
		var receiverOptions = ReceiverOptions
				.<Long, HeartRate>create(consumerProperties)
				.subscription(Collections.singletonList("heart-rates"))
				// just for logging purpose
				.addAssignListener(partitions -> logger.info("onPartitionsAssigned {}", partitions))
				.addRevokeListener(partitions -> logger.info("onPartitionsRevoked {}", partitions));
		return KafkaReceiver.create(receiverOptions)
							.receive()
							.filter(r -> userId == r.key())
							.map(ConsumerRecord::value);
	}
}
