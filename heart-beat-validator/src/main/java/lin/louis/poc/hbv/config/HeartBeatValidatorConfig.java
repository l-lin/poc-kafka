package lin.louis.poc.hbv.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.KafkaStreamBrancher;

import lin.louis.poc.hbv.predicate.ValidHeartBeatPredicate;
import lin.louis.poc.models.HeartBeat;


@Configuration
@EnableKafkaStreams
public class HeartBeatValidatorConfig {

	@Bean
	@ConfigurationProperties(prefix = "heart-beat")
	HeartBeatProperties heartBeatProperties() {
		return new HeartBeatProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "topics")
	TopicsProperties topicProperties() {
		return new TopicsProperties();
	}

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

	@Bean
	KStream<Long, HeartBeat> kStream(StreamsBuilder streamsBuilder,
			HeartBeatProperties heartBeatProperties,
			TopicsProperties topicsProperties) {
		KStream<Long, HeartBeat> stream = streamsBuilder.stream(topicsProperties.getFrom());
		stream.print(Printed.toSysOut());
		return new KafkaStreamBrancher<Long, HeartBeat>()
				.branch(new ValidHeartBeatPredicate(
								heartBeatProperties.getHri().getMin(),
								heartBeatProperties.getHri().getMax()
						),
						kStream -> kStream.to(topicsProperties.getTo().getValid().getName()))
				.defaultBranch(kStream -> kStream.to(topicsProperties.getTo().getInvalid().getName()))
				.onTopOf(stream);
	}
}
