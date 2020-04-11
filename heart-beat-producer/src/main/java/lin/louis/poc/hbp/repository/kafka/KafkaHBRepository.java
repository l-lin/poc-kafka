package lin.louis.poc.hbp.repository.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import lin.louis.poc.hbp.repository.HBRepository;
import lin.louis.poc.models.HeartBeat;


/**
 * Simple Spring Kafka producer implementation.
 *
 * @see <a href="https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#kafka-template">Spring Kafka
 * documentation</a>
 */
public class KafkaHBRepository implements HBRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String topicName;

	private final KafkaTemplate<Long, HeartBeat> kafkaTemplate;

	public KafkaHBRepository(String topicName, KafkaTemplate<Long, HeartBeat> kafkaTemplate) {
		this.topicName = topicName;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void save(HeartBeat heartBeat) {
		logger.debug(
				"Sending to kafka topic '{}' the following heart beat in key {}: {}",
				topicName,
				heartBeat.getUserId(),
				heartBeat
		);
		// using the userId as the topic key, so I can easily aggregate them afterwards
		kafkaTemplate.send(topicName, heartBeat.getUserId(), heartBeat);
	}
}
