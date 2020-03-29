package lin.louis.poc.hbp.repository.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import lin.louis.poc.hbp.repository.HeartBeatRepository;
import lin.louis.poc.models.HeartBeat;


public class HeartBeatKafkaProducer implements HeartBeatRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final KafkaTemplate<String, HeartBeat> kafkaTemplate;

	public HeartBeatKafkaProducer(KafkaTemplate<String, HeartBeat> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void save(HeartBeat heartBeat) {
		logger.info("Sending to kafka the following heart beat: '{}'", heartBeat);
		kafkaTemplate.sendDefault(heartBeat);
	}
}
