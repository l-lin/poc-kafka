package lin.louis.poc.hrc.config;

import java.util.Arrays;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

import lin.louis.poc.hrc.stream.HRComputorStreamBuilder;
import lin.louis.poc.hrc.factory.HRFactory;
import lin.louis.poc.hrc.factory.valuecomputor.HRValueComputor;
import lin.louis.poc.hrc.factory.reset.GapResetChecker;
import lin.louis.poc.hrc.factory.reset.HriResetChecker;
import lin.louis.poc.hrc.factory.reset.QRSResetChecker;
import lin.louis.poc.hrc.factory.reset.ResetCheckerFacade;
import lin.louis.poc.hrc.factory.reset.TimestampResetChecker;
import lin.louis.poc.models.HeartRate;


@Configuration
@EnableKafkaStreams
public class HRComputorConfig {
	@Bean
	@ConfigurationProperties(prefix = "topics")
	TopicsProperties topicProperties() {
		return new TopicsProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "heart-rate")
	HRProperties hrProperties() {
		return new HRProperties();
	}

	@Bean
	NewTopic topicHeartRates(TopicsProperties topicsProperties) {
		TopicsProperties.To t = topicsProperties.getTo();
		return TopicBuilder.name(t.getName())
						   .partitions(t.getPartitions())
						   .replicas(t.getReplicas())
						   .build();
	}

	@Bean
	ResetCheckerFacade resetCheckerFacade(HRProperties hrProperties) {
		return new ResetCheckerFacade(Arrays.asList(
				new GapResetChecker(hrProperties.getGapDuration()),
				new HriResetChecker(hrProperties.getHri().getMin(), hrProperties.getHri().getMax()),
				new QRSResetChecker(),
				new TimestampResetChecker()
		));
	}

	@Bean
	HRValueComputor hrValueComputor() {
		return new HRValueComputor();
	}

	@Bean
	HRFactory hrFactory(
			HRProperties hrProperties,
			ResetCheckerFacade resetCheckerFacade,
			HRValueComputor hrValueComputor
	) {
		return new HRFactory(
				hrProperties.getNbHeartBeats(),
				resetCheckerFacade,
				hrValueComputor
		);
	}

	@Bean
	KStream<Long, HeartRate> kStream(
			StreamsBuilder streamsBuilder,
			TopicsProperties topicsProperties,
			HRFactory hrFactory,
			HRProperties hrProperties
	) {
		return HRComputorStreamBuilder.withStreamsBuilder(streamsBuilder)
									  .from(topicsProperties.getFrom())
									  .to(topicsProperties.getTo().getName())
									  .withHRFactory(hrFactory)
									  .heartRateComputedBy(hrProperties.getNbHeartBeats())
									  .buildKStream();
	}
}
