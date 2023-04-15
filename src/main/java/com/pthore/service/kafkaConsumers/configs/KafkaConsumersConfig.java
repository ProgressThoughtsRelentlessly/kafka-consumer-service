package com.pthore.service.kafkaConsumers.configs;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.pthore.service.kafkaConsumers.utils.AppConstants;

@Configuration
public class KafkaConsumersConfig {
	
	public Properties getProperties(String groupId) {
		
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConstants.BOOTSTRAP_SEVER);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return properties;
	}
	
	
	@Bean(name = "user_post_analytics_consumer")
	@Scope(scopeName = "singleton")
	public KafkaConsumer<String, Long> getMostViewedPostCountConsumer() {
		KafkaConsumer<String, Long> kafkaConsumer = new KafkaConsumer<String, Long>(getProperties("user_post_analytics_consumer"));
		
		// CURRENTLY SUBSCRIBED TO 6 TOPICS FOR ANALYTICS.
		kafkaConsumer.subscribe(Arrays.asList(
				"most-viewed-post-count", 
				"most-viewed-profile-count",
				"most-upvoted-post-count",
				"most-searched-keyword-count",
				"most-popular-domain-count",
				"peak-user-activity-range"
				));
		return kafkaConsumer;
	}
/*	
	@Bean(name = "most_viewed_profile_count_consumer")
	@Scope(scopeName = "singleton")
	public KafkaConsumer<String, Long> getMostViewedProfileCountConsumer() {
		KafkaConsumer<String, Long> kafkaConsumer = new KafkaConsumer<String, Long>(getProperties("most_viewed_profile_count_consumer"));
		kafkaConsumer.subscribe(Collections.singletonList(""));
		return kafkaConsumer;
	}
	
	
	@Bean(name = "most_upvoted_post_count_consumer")
	@Scope(scopeName = "singleton")
	public KafkaConsumer<String, Long> getMostUpvotedPostCountConsumer() {
		KafkaConsumer<String, Long> kafkaConsumer = new KafkaConsumer<String, Long>(getProperties("most_upvoted_post_count_consumer"));
		kafkaConsumer.subscribe(Collections.singletonList(""));
		return kafkaConsumer;
	}
	
	@Bean(name = "most_searched_keyword_count_consumer")
	@Scope(scopeName = "singleton")
	public KafkaConsumer<String, Long> getMostSearchedKeywordCountConsumer() {
		KafkaConsumer<String, Long> kafkaConsumer = new KafkaConsumer<String, Long>(getProperties("most_searched_keyword_count_consumer"));
		kafkaConsumer.subscribe(Collections.singletonList(""));
		return kafkaConsumer;
	}
	
	@Bean(name = "most_popular_domain_count_consumer")
	@Scope(scopeName = "singleton")
	public KafkaConsumer<String, Long> getMostPopularDomainCountConsumer() {
		KafkaConsumer<String, Long> kafkaConsumer = new KafkaConsumer<String, Long>(getProperties("most_popular_domain_count_consumer"));
		kafkaConsumer.subscribe(Collections.singletonList(""));
		return kafkaConsumer;
	}
	@Bean(name = "peak_user_activity_range_consumer")
	@Scope(scopeName = "singleton")
	public KafkaConsumer<String, Long> getPeekUserActivityRangeConsumer() {
		KafkaConsumer<String, Long> kafkaConsumer = new KafkaConsumer<String, Long>(getProperties("peak_user_activity_range_consumer"));
		kafkaConsumer.subscribe(Collections.singletonList(""));
		return kafkaConsumer;
	}
*/	
	
}
