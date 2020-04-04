package lin.louis.poc.hbp.repository.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import lin.louis.poc.hbp.repository.HBRepository;
import lin.louis.poc.models.HeartBeat;


public class HBKafkaProducer implements HBRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String topicName;

	private final KafkaTemplate<Long, HeartBeat> kafkaTemplate;

	public HBKafkaProducer(String topicName, KafkaTemplate<Long, HeartBeat> kafkaTemplate) {
		this.topicName = topicName;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void save(HeartBeat heartBeat) {
		logger.info("Sending to kafka topic '{}' the following heart beat in key '{}': '{}'",
				topicName,
				heartBeat.getUserId(),
				heartBeat);
		kafkaTemplate.send(topicName, heartBeat.getUserId(), heartBeat);
	}
}
