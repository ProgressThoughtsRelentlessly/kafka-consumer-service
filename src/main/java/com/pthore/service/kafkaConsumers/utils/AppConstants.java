package com.pthore.service.kafkaConsumers.utils;

import java.util.Arrays;
import java.util.List;

public interface AppConstants {
	
	public final String BOOTSTRAP_SEVER = "127.0.0.1:9092";
	public final String ZOOKEEPER_SERVER = "127.0.0.1:2181";
	public final int MIN_HEAP_SIZE = 20;
	public final int TIME_RANGE_INTERVALS = 6;
	public final List<String> ANALYTICS_OUTPUT_TOPIC_NAMES = Arrays.asList(
			"most-viewed-post-count", 
			"most-viewed-profile-count",
			"most-upvoted-post-count",
			"most-searched-keyword-count",
			"most-popular-domain-count",
			"peak-user-activity-range");
	public final String REDIS_CONSUMER_ANALYSIS_HASH_KEY = "consumerAnalysis";
}
