package com.pthore.service.kafkaConsumers.tasks;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.pthore.service.kafkaConsumers.models.PostUpvoteCount;
import com.pthore.service.kafkaConsumers.models.UserActivityTimeRange;
import com.pthore.service.kafkaConsumers.services.MinHeapImpl;

public class PostUpvoteCountProcessorTask implements Runnable {

	private PostUpvoteCount[] array;
	private Iterable<ConsumerRecord<String, Long>> records;
	private Map<String, Integer> mostUpvotedPostsMap;

	public PostUpvoteCountProcessorTask (PostUpvoteCount[] array, Iterable<ConsumerRecord<String, Long>> records, Map<String, Integer> mostUpvotedPostsMap) {
		this.array = array;		
		this.records = records;
	}

	@Override
	public void run() {

		MinHeapImpl<PostUpvoteCount> minHeap = new MinHeapImpl<>(mostUpvotedPostsMap);
		minHeap.setArray(this.array);
		
		this.records.forEach(record -> {
			
			PostUpvoteCount postUpvoteCount = new PostUpvoteCount();
			postUpvoteCount.setPostId(record.key()) ;
			postUpvoteCount.setUpvoteCount(record.value());
			
			PostUpvoteCount min = minHeap.getMin();
			if(min == null)
				minHeap.insert(postUpvoteCount);
			else if( mostUpvotedPostsMap.containsKey(postUpvoteCount.get_id()))
				minHeap.updateFrequency(postUpvoteCount);
			else if( !mostUpvotedPostsMap.containsKey(postUpvoteCount.get_id()) && min.getUpvoteCount() < postUpvoteCount.getUpvoteCount()) {
				minHeap.insert(postUpvoteCount);
			}
		});
		
	}

}
