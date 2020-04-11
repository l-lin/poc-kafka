package lin.louis.poc.hbv.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

import lin.louis.poc.hbv.predicate.ValidHBPredicate;
import lin.louis.poc.hbv.stream.HBValidatorStreamBuilder;
import lin.louis.poc.models.HeartBeat;


@Configuration
@EnableKafkaStreams
public class HBValidatorConfig {

	@Bean
	@ConfigurationProperties(prefix = "topics")
	TopicsProperties topicProperties() {
		return new TopicsProperties();
	}

	/**
	 * Create a new topic on startup if not exists.
	 *
	 * @see <a href="https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#configuring-topics">Spring
	 * Kafka documentation</a>
	 */
	@Bean
	NewTopic topicHeartBeatsValid(TopicsProperties topicsProperties) {
		TopicsProperties.Topic t = topicsProperties.getTo().getValid();
		return TopicBuilder.name(t.getName())
						   .partitions(t.getPartitions())
						   .replicas(t.getReplicas())
						   .build();
	}

	@Bean
	NewTopic topicHeartBeatsInvalid(TopicsProperties topicsProperties) {
		TopicsProperties.Topic t = topicsProperties.getTo().getInvalid();
		return TopicBuilder.name(t.getName())
						   .partitions(t.getPartitions())
						   .replicas(t.getReplicas())
						   .build();
	}

	/**
	 * Using Spring KStream to stream heart beats into valid and invalid ones.
	 *
	 * @see <a href="https://docs.spring.io/spring-kafka/docs/2.3.7.RELEASE/reference/html/#kafka-streams-example">Spring
	 * kafka streams example</a>
	 */
	@Bean
	KStream<Long, HeartBeat> kStream(StreamsBuilder streamsBuilder, TopicsProperties topicsProperties) {
		return HBValidatorStreamBuilder.withStreamsBuilder(streamsBuilder)
									   .from(topicsProperties.getFrom())
									   .to(
											   topicsProperties.getTo().getValid().getName(),
											   topicsProperties.getTo().getInvalid().getName()
									   )
									   .withPredicate(new ValidHBPredicate())
									   .buildKStream();
	}
}
