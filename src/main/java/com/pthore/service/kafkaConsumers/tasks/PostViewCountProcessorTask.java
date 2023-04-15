package com.pthore.service.kafkaConsumers.tasks;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.pthore.service.kafkaConsumers.models.PostViewCount;
import com.pthore.service.kafkaConsumers.services.MinHeapImpl;

public class PostViewCountProcessorTask implements Runnable {
	
	private PostViewCount[] array;
	private Iterable<ConsumerRecord<String, Long>> records;
	private Map<String, Integer> mostViewedPostMap;
	
	public PostViewCountProcessorTask(PostViewCount[] array, Iterable<ConsumerRecord<String, Long>> records, Map<String, Integer> mostViewedPostMap) {
		this.array = array;		
		this.records = records;
		this.mostViewedPostMap = mostViewedPostMap;
	}

	@Override
	public void run() {

		MinHeapImpl<PostViewCount> minHeap = new MinHeapImpl<>(mostViewedPostMap);
		minHeap.setArray(this.array);
		
		this.records.forEach(record -> {
			
			PostViewCount postCount = new PostViewCount();
			postCount.setPostId(record.key());
			postCount.setPostViewCount(record.value());
			
			PostViewCount min = minHeap.getMin();
			if(min == null)
				minHeap.insert(postCount);
			else if( mostViewedPostMap.containsKey(postCount.get_id()))
				minHeap.updateFrequency(postCount);
			else if( !mostViewedPostMap.containsKey(postCount.get_id()) && min.getPostViewCount() < postCount.getPostViewCount()) {
				minHeap.insert(postCount);
			}
		});
		
	}
	
	
	
	
}
