package com.pthore.service.kafkaConsumers.tasks;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.pthore.service.kafkaConsumers.models.UserActivityTimeRange;
import com.pthore.service.kafkaConsumers.services.MinHeapImpl;

public class UserActivityTimeRangeCountProcessorTask implements Runnable {

	private UserActivityTimeRange[] array;
	private Iterable<ConsumerRecord<String, Long>> records;
	private Map<String, Integer> mostActiveTimeMap;

	public UserActivityTimeRangeCountProcessorTask (UserActivityTimeRange[] array, Iterable<ConsumerRecord<String, Long>> records, Map<String, Integer> mostActiveTimeMap) {
		this.array = array;		
		this.records = records;
	}

	@Override
	public void run() {

		MinHeapImpl<UserActivityTimeRange> minHeap = new MinHeapImpl<>(mostActiveTimeMap);
		minHeap.setArray(this.array);
		
		this.records.forEach(record -> {
			
			UserActivityTimeRange userActivityRange = new UserActivityTimeRange();
			userActivityRange.setActiveTimeRange(record.key()) ;
			userActivityRange.setActivityCount(record.value());
			
			UserActivityTimeRange min = minHeap.getMin();
			if(min == null)
				minHeap.insert(userActivityRange);
			else if( mostActiveTimeMap.containsKey(userActivityRange.get_id()))
				minHeap.updateFrequency(userActivityRange);
			else if( !mostActiveTimeMap.containsKey(userActivityRange.get_id()) && min.getActivityCount() < userActivityRange.getActivityCount()) {
				minHeap.insert(userActivityRange);
			}
		});
	}
}
