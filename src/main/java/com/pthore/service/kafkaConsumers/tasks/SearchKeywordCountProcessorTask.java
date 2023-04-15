package com.pthore.service.kafkaConsumers.tasks;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.pthore.service.kafkaConsumers.models.SearchedKeywordCount;
import com.pthore.service.kafkaConsumers.services.MinHeapImpl;

public class SearchKeywordCountProcessorTask implements Runnable {

	private SearchedKeywordCount[] array;
	private Iterable<ConsumerRecord<String, Long>> records;
	private Map<String, Integer> mostSearchedMap;
	
	public SearchKeywordCountProcessorTask(SearchedKeywordCount[] array, Iterable<ConsumerRecord<String, Long>> records, Map<String, Integer> mostSearchedMap) {
		this.array = array;		
		this.records = records;
	}

	@Override
	public void run() {

		MinHeapImpl<SearchedKeywordCount> minHeap = new MinHeapImpl<>(mostSearchedMap);
		minHeap.setArray(this.array);
		
		this.records.forEach(record -> {
			
			SearchedKeywordCount searchedKeywordCount = new SearchedKeywordCount();
			searchedKeywordCount.setKeyword(record.key()); 
			searchedKeywordCount.setUsageCount(record.value()); 
			
			SearchedKeywordCount min = minHeap.getMin();
			if(min == null)
				minHeap.insert(searchedKeywordCount);
			else if( mostSearchedMap.containsKey(searchedKeywordCount.get_id()))
				minHeap.updateFrequency(searchedKeywordCount);
			else if( !mostSearchedMap.containsKey(searchedKeywordCount.get_id()) && min.getUsageCount() < searchedKeywordCount.getUsageCount()) {
				minHeap.insert(searchedKeywordCount);
			}
		});
		
	}
}
