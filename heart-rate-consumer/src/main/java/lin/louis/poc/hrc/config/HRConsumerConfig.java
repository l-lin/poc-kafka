package lin.louis.poc.hrc.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lin.louis.poc.hrc.repository.HRRepository;
import lin.louis.poc.hrc.repository.kafka.HRKafkaConsumer;
import lin.louis.poc.hrc.service.HRFetcher;


@Configuration
public class HRConsumerConfig {
	@Bean
	@ConfigurationProperties(prefix = "topic")
	TopicProperties topicProperties() {
		return new TopicProperties();
	}

	@Bean
	HRRepository hrRepository(KafkaProperties kafkaProperties) {
		return new HRKafkaConsumer(kafkaProperties);
	}

	@Bean
	HRFetcher hrFetcher(HRRepository hrRepository) {
		return new HRFetcher(hrRepository);
	}
}
