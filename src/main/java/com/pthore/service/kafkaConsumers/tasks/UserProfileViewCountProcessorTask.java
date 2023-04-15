package com.pthore.service.kafkaConsumers.tasks;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.pthore.service.kafkaConsumers.models.UserProfileCount;
import com.pthore.service.kafkaConsumers.services.MinHeapImpl;

public class UserProfileViewCountProcessorTask implements Runnable {

	private UserProfileCount[] array;
	private Iterable<ConsumerRecord<String, Long>> records;
	private Map<String, Integer> mostViewedProfileMap;
	
	public UserProfileViewCountProcessorTask(UserProfileCount[] array, Iterable<ConsumerRecord<String, Long>> records, Map<String, Integer> mostViewedUserMap) {
		this.array = array;		
		this.records = records;
	}

	@Override
	public void run() {

		MinHeapImpl<UserProfileCount> minHeap = new MinHeapImpl<>(mostViewedProfileMap);
		minHeap.setArray(this.array);
		
		this.records.forEach(record -> {
			
			UserProfileCount profileViewCount = new UserProfileCount();
			profileViewCount.setUserEmail(record.key());
			profileViewCount.setViewCount(record.value()); 
			
			UserProfileCount min = minHeap.getMin();
			if(min == null)
				minHeap.insert(profileViewCount);
			else if( mostViewedProfileMap.containsKey(profileViewCount.get_id()))
				minHeap.updateFrequency(profileViewCount);
			else if( !mostViewedProfileMap.containsKey(profileViewCount.get_id()) && min.getViewCount() < profileViewCount.getViewCount()) {
				minHeap.insert(profileViewCount);
			}
			
		});
		
	}
}
