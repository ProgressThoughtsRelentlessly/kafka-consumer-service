package com.pthore.service.kafkaConsumers.tasks;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.pthore.service.kafkaConsumers.models.DomainViewCount;
import com.pthore.service.kafkaConsumers.models.PostUpvoteCount;
import com.pthore.service.kafkaConsumers.services.MinHeapImpl;

public class DomainViewCountProcessorTask implements Runnable {

	private DomainViewCount[] array;
	private Iterable<ConsumerRecord<String, Long>> records;
	private Map<String, Integer> mostViewedDomainsMap;
	
	public DomainViewCountProcessorTask (DomainViewCount[] array, Iterable<ConsumerRecord<String, Long>> records, Map<String, Integer> mostViewedDomainsMap) {
		this.array = array;		
		this.records = records;
	}

	@Override
	public void run() {

		MinHeapImpl<DomainViewCount> minHeap = new MinHeapImpl<>(mostViewedDomainsMap);
		minHeap.setArray(this.array);
		
		this.records.forEach(record -> {
			
			DomainViewCount domainViewCount = new DomainViewCount();
			domainViewCount.setDomainName(record.key()) ;
			domainViewCount.setViewCount(record.value());
			
			DomainViewCount min = minHeap.getMin();
			if(min == null)
				minHeap.insert(domainViewCount);
			else if( mostViewedDomainsMap.containsKey(domainViewCount.get_id()))
				minHeap.updateFrequency(domainViewCount);
			else if( !mostViewedDomainsMap.containsKey(domainViewCount.get_id()) && min.getViewCount() < domainViewCount.getViewCount()) {
				minHeap.insert(domainViewCount);
			}
		});
		
	}

}
