package com.pthore.service.kafkaConsumers.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pthore.service.kafkaConsumers.models.DomainViewCount;
import com.pthore.service.kafkaConsumers.models.AConsumerObject;
import com.pthore.service.kafkaConsumers.models.PostUpvoteCount;
import com.pthore.service.kafkaConsumers.models.PostViewCount;
import com.pthore.service.kafkaConsumers.models.SearchedKeywordCount;
import com.pthore.service.kafkaConsumers.models.UserActivityTimeRange;
import com.pthore.service.kafkaConsumers.models.UserProfileCount;
import com.pthore.service.kafkaConsumers.tasks.DomainViewCountProcessorTask;
import com.pthore.service.kafkaConsumers.tasks.PostUpvoteCountProcessorTask;
import com.pthore.service.kafkaConsumers.tasks.PostViewCountProcessorTask;
import com.pthore.service.kafkaConsumers.tasks.SearchKeywordCountProcessorTask;
import com.pthore.service.kafkaConsumers.tasks.UserActivityTimeRangeCountProcessorTask;
import com.pthore.service.kafkaConsumers.tasks.UserProfileViewCountProcessorTask;
import com.pthore.service.kafkaConsumers.utils.AppConstants;

@Service
@Scope("singleton")
public class KafkaConsumerService {

	@Autowired
	@Qualifier(value = "user_post_analytics_consumer")
	private KafkaConsumer<String, Long> userPostAnalyticsConsumer;
	
	@Autowired
	@Qualifier(value = "redisTemplateForIConsumerObject")
	private RedisTemplate<String, AConsumerObject<?>> redisTemplate;
	
	private Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
	// This is for concurrent processing of processing kafka-consumer-fetched records.
	private ThreadPoolExecutor threadPoolExecutors;
	private PostViewCount[] mostViewedPosts;
	private UserProfileCount[] mostViewdUserProfiles;
	private SearchedKeywordCount[] mostSearchedKeywords;
	private UserActivityTimeRange[] mostActiveTimeRanges;
	private PostUpvoteCount[] mostUpvotedPosts;
	private DomainViewCount[] mostViewedDomains;
	
	private Map<String, Integer> mostViewedPostMap;
	private Map<String, Integer> mostViewedUserMap; // needed to update the frequency.
	private Map<String, Integer> mostSearchedMap;
	private Map<String, Integer> mostActiveTimeMap;
	private Map<String, Integer> mostUpvotedPostsMap;
	private Map<String, Integer> mostViewedDomainsMap;
	
	
	
	
	public KafkaConsumerService() {
		mostViewedPosts = new PostViewCount[AppConstants.MIN_HEAP_SIZE];
		mostViewdUserProfiles = new UserProfileCount[AppConstants.MIN_HEAP_SIZE];
		mostSearchedKeywords = new SearchedKeywordCount[AppConstants.MIN_HEAP_SIZE];
		mostActiveTimeRanges = new UserActivityTimeRange[AppConstants.TIME_RANGE_INTERVALS];
		mostUpvotedPosts = new PostUpvoteCount[AppConstants.MIN_HEAP_SIZE];
		mostViewedDomains = new DomainViewCount[AppConstants.MIN_HEAP_SIZE];
		
		mostViewedPostMap = new HashMap<>();
		mostViewedUserMap = new HashMap<>();
		mostSearchedMap = new HashMap<>();
		mostActiveTimeMap = new HashMap<>();
		mostUpvotedPostsMap = new HashMap<>();
		mostViewedDomainsMap = new HashMap<>();
		
		this.threadPoolExecutors = new ThreadPoolExecutor(6, 12, 1200, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(24));
		
	}

	
	@PreDestroy
	public void closeAllResources() {
		userPostAnalyticsConsumer.close(Duration.ofMillis(100));
		threadPoolExecutors.shutdownNow();
	}
	
	@Async
	@Scheduled(fixedRate = 3_000, initialDelay = (2 * 1_000)) // scheduled to run for every 3 seconds. with initial dealy of 2 sec
	public void pollRecordsFromAllRegisteredTopics () {
		
		ConsumerRecords<String, Long> consumerRecords = userPostAnalyticsConsumer.poll(Duration.ofMillis(2800)); // every 2.8 seconds

		Iterable<ConsumerRecord<String, Long>> postViewCountRecords = consumerRecords.records("most-viewed-post-count");
		Iterable<ConsumerRecord<String, Long>> profileViewCountRecords = consumerRecords.records("most-viewed-profile-count");
		Iterable<ConsumerRecord<String, Long>> searchKeywordCountRecords = consumerRecords.records("most-searched-keyword-count");
		Iterable<ConsumerRecord<String, Long>> activeTimeRangeRecords = consumerRecords.records("peak-user-activity-range");
		Iterable<ConsumerRecord<String, Long>> upvotedPostCountRecords = consumerRecords.records("most-upvoted-post-count");
		Iterable<ConsumerRecord<String, Long>> domainViewCountRecords = consumerRecords.records("most-popular-domain-count");
		
		/* *********************  THE BELOW TASKS ARE LFU CACHE IMPLEMENTATION USING MINHEAP.  ******************************  */
		PostViewCountProcessorTask task1 = new PostViewCountProcessorTask(mostViewedPosts, postViewCountRecords, mostViewedPostMap);
		UserProfileViewCountProcessorTask task2 = new UserProfileViewCountProcessorTask(mostViewdUserProfiles, profileViewCountRecords, mostViewedUserMap);
		SearchKeywordCountProcessorTask task3 = new SearchKeywordCountProcessorTask(mostSearchedKeywords, searchKeywordCountRecords, mostSearchedMap);
		UserActivityTimeRangeCountProcessorTask task4 = new UserActivityTimeRangeCountProcessorTask(mostActiveTimeRanges, activeTimeRangeRecords, mostActiveTimeMap);
		PostUpvoteCountProcessorTask task5 = new PostUpvoteCountProcessorTask(mostUpvotedPosts, upvotedPostCountRecords, mostUpvotedPostsMap);
		DomainViewCountProcessorTask task6 = new DomainViewCountProcessorTask(mostViewedDomains, domainViewCountRecords, mostViewedDomainsMap);
		
		threadPoolExecutors.execute(task1);
		threadPoolExecutors.execute(task2);
		threadPoolExecutors.execute(task3);
		threadPoolExecutors.execute(task4);
		threadPoolExecutors.execute(task5);
		threadPoolExecutors.execute(task6);			
		
	}
	
	// The Async thread are given max-priority.
	@Async
	@Scheduled(cron = "* */2 * * * *", initialDelay = (2 * 1_000)) // CRON SET TO EVERY 2 MINUTES.
	public void syncToRedisDatabase() {
		
		
		final List<String> topicNames = AppConstants.ANALYTICS_OUTPUT_TOPIC_NAMES;
		final ListOperations<String, AConsumerObject<?>> listOperation = redisTemplate.opsForList();
		long size;
		for(int i = 0; i < topicNames.size(); i++) {
			
			final String TOPIC_NAME = topicNames.get(i);
			
			switch(TOPIC_NAME) {
			case "most-viewed-post-count":
				size = listOperation.size("most-viewed-post-count");
				if(size > 0) {
					while(size --> 0) {
						AConsumerObject<?> listDeleteResult = listOperation.leftPop("most-viewed-post-count");
						logger.info("Deleted elements ", listDeleteResult);
					}
				}
				List<AConsumerObject<?>> listToPush = new ArrayList<>();
				listToPush.addAll(Arrays.asList(mostViewedPosts).stream().filter(p -> p != null).collect(Collectors.toList()) );
				if(listToPush.size() > 0) {
					listOperation.leftPushAll("most-viewed-post-count", listToPush);
				}
				break;
				
			case "most-viewed-profile-count":
				size = listOperation.size("most-viewed-profile-count");
				if(size > 0) {
					while(size --> 0) {
						AConsumerObject<?> listDeleteResult = listOperation.leftPop("most-viewed-profile-count");
						logger.info("Deleted elements ", listDeleteResult);
					}
				}
				listToPush = new ArrayList<>();
				listToPush.addAll(Arrays.asList(mostViewdUserProfiles).stream().filter(p -> p != null).collect(Collectors.toList()));
				if(listToPush.size() > 0) {
					listOperation.leftPushAll("most-viewed-profile-count", listToPush);
				}
				break;
			case "most-upvoted-post-count":
				
				size = listOperation.size("most-upvoted-post-count");
				if(size > 0) {
					while(size --> 0) {
						AConsumerObject<?> listDeleteResult = listOperation.leftPop("most-upvoted-post-count");
						logger.info("Deleted elements ", listDeleteResult);
					}
				}
				listToPush = new ArrayList<>();
				listToPush.addAll(Arrays.asList(mostUpvotedPosts).stream().filter(p -> p != null).collect(Collectors.toList()));
				if(listToPush.size() > 0) {
					listOperation.leftPushAll("most-upvoted-post-count", listToPush);
				}
				break;
			case "most-searched-keyword-count":
				
				size = listOperation.size("most-searched-keyword-count");
				if(size > 0) {
					while(size --> 0) {
						AConsumerObject<?> listDeleteResult = listOperation.leftPop("most-searched-keyword-count");
						logger.info("Deleted elements ", listDeleteResult);
					}
				}
				listToPush = new ArrayList<>();
				listToPush.addAll(Arrays.asList(mostSearchedKeywords).stream().filter(p -> p != null).collect(Collectors.toList()));
				if(listToPush.size() > 0) {
					listOperation.leftPushAll("most-searched-keyword-count", listToPush);
				}
				break;
			case "most-popular-domain-count":

				size = listOperation.size("most-popular-domain-count");
				if(size > 0) {
					while(size --> 0) {
						AConsumerObject<?> listDeleteResult = listOperation.leftPop("most-popular-domain-count");
						logger.info("Deleted elements ", listDeleteResult);
					}
				}
				listToPush = new ArrayList<>();
				listToPush.addAll(Arrays.asList(mostViewedDomains).stream().filter(p -> p != null).collect(Collectors.toList()));
				if(listToPush.size() > 0) {
					listOperation.leftPushAll("most-popular-domain-count", listToPush);
				}
				break;
			case "peak-user-activity-range":
				size = listOperation.size("peak-user-activity-range");
				if(size > 0) {
					while(size --> 0) {
						AConsumerObject<?> listDeleteResult = listOperation.leftPop("peak-user-activity-range");
						logger.info("Deleted elements ", listDeleteResult);
					}
				}
				listToPush = new ArrayList<>();
				listToPush.addAll(Arrays.asList(mostActiveTimeRanges).stream().filter(p -> p != null).collect(Collectors.toList()));
				if(listToPush.size() > 0) {
					listOperation.leftPushAll("peak-user-activity-range", listToPush);
				}
				break;
			
			}

		}
		
	}
}
