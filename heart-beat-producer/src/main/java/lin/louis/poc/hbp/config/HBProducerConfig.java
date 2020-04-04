package lin.louis.poc.hbp.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import lin.louis.poc.hbp.repository.HBRepository;
import lin.louis.poc.hbp.repository.kafka.HBKafkaProducer;
import lin.louis.poc.models.HeartBeat;


@Configuration
public class HBProducerConfig {

	@Bean
	@ConfigurationProperties(prefix = "topic")
	TopicProperties topicProperties() {
		return new TopicProperties();
	}

	@Bean
	NewTopic newTopic(TopicProperties topicProperties) {
		return TopicBuilder.name(topicProperties.getName())
				.partitions(topicProperties.getPartitions())
				.replicas(topicProperties.getReplicas())
				.compact()
				.build();
	}

	@Bean
	HBRepository heartBeatRepository(TopicProperties topicProperties, KafkaTemplate<Long, HeartBeat> kafkaTemplate) {
		return new HBKafkaProducer(topicProperties.getName(), kafkaTemplate);
	}
}
