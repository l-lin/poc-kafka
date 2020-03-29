package lin.louis.poc.hbp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import lin.louis.poc.hbp.repository.HeartBeatRepository;
import lin.louis.poc.hbp.repository.kafka.HeartBeatKafkaProducer;
import lin.louis.poc.models.HeartBeat;


@Configuration
public class HeartBeatProducerConfig {

	@Bean
	NewTopic newTopic(@Value("${spring.kafka.template.default-topic}") String topic) {
		return TopicBuilder.name(topic).build();
	}

	@Bean
	HeartBeatRepository heartBeatRepository(KafkaTemplate<String, HeartBeat> kafkaTemplate) {
		return new HeartBeatKafkaProducer(kafkaTemplate);
	}
}
