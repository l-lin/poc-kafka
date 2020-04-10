package lin.louis.poc.hrc.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lin.louis.poc.hrc.repository.HRFluxRepository;
import lin.louis.poc.hrc.repository.kafka.KafkaHRFluxRepository;
import lin.louis.poc.hrc.service.HRFetcher;


@Configuration
public class HRConsumerConfig {
	@Bean
	@ConfigurationProperties(prefix = "topic")
	TopicProperties topicProperties() {
		return new TopicProperties();
	}

	@Bean
	HRFluxRepository hrRepository(KafkaProperties kafkaProperties) {
		return new KafkaHRFluxRepository(kafkaProperties);
	}

	@Bean
	HRFetcher hrFetcher(HRFluxRepository hrFluxRepository) {
		return new HRFetcher(hrFluxRepository);
	}
}
