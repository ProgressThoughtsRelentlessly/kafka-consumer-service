package com.pthore.service.kafkaConsumers.services;


import java.util.Map;

import com.pthore.service.kafkaConsumers.models.AConsumerObject;
import com.pthore.service.kafkaConsumers.models.UserActivityTimeRange;
import com.pthore.service.kafkaConsumers.utils.AppConstants;

/*
algorithm:
	O(logN) time | O(N) space

design:
	. heapify from top to bottom. In minHeap fashion.
	
	insert:
		. you need to append it to the last index in the current sequence.
		. take care of size and capacity constraints | edge cases.
	evict:
		. move element at the last index to the first and Heapify it.
		. take care of 'size'. thats it.
	when you are inserting and the size is full: 
		. then evict() and 
		. insert to the top

*/

public class MinHeapImpl<T extends AConsumerObject<T>> {
	
	private T[] array;
	private int size = 0;
	private int capacity = AppConstants.MIN_HEAP_SIZE;
	private Map<String, Integer> indexMap;
	
	public MinHeapImpl(Map<String, Integer> indexMap) {
		this.indexMap = indexMap;
	}
	
    private void swap(int i, int j) {
        T t = this.array[i];
        this.array[i] = this.array[j];
        this.array[j] = t;
        
        indexMap.put(array[i].get_id(), j);
        indexMap.put(array[j].get_id() , i);
    }
    
    // FOR ARRAY WITH SMALLER SIZE
    private final void heapifyRecursiveApproach(int index) { // MINHEAP
		int parent = index;
		int left = 2 * parent + 1;
		int right = 2 * parent + 2;
		
		if(left < size && array[parent].compareTo(array[left]) > 0) 
			parent = left;
		if(right < size && array[parent].compareTo(array[right]) > 0)
			parent = right;
		if(parent != index) {
			swap(parent, index);
			heapifyRecursiveApproach(parent);
		}
		
	}
	
	// FOR ARRAY WITH BIGGER SIZE: MIN-HEAP
	private final void heapifyItertiveApproach(int index) {
		for(int i = index; i < size; ) {
			int p = i;
			int left = 2 * i + 1;
			int right = 2 * i + 2;
			if(left < size && array[p].compareTo(array[left]) > 0) 
				p = left;
			if(right < size && array[p].compareTo(array[right]) > 0)
				p = right;
			if(p != i) {
				swap(p, i);
				i = p;
			} else {
				return;
			}
		}
	}
	
	// THIS IS HEAPIFYING BY SIFING UPWARDS.  
	private final void buildHeapSiftUp(int index) {
		int parentIndex = (index - 1) / 2;  // note: this index won't go beyond 0. notice carefully.
		for(int i = index; parentIndex >= 0 && (array[i].compareTo(array[parentIndex]) < 0); ) {
			swap(i, parentIndex);
			i = parentIndex;
			parentIndex = (i -1) / 2;
		}
	}
	
	private final void buildHeapSiftDown(int idx) {
		int parentIdx = (idx -1) / 2;
		for(int i = parentIdx; i >= 0; i--) {
			this.heapifyRecursiveApproach(i);
		}
	}
	
	public final void insert(T obj) {
		if(size == capacity) {
			evictAndInsert(obj);
		} else {
			size++;
			array[size -1] = obj;
			this.indexMap.put(obj.get_id(), size -1);
			buildHeapSiftUp(size -1);
		}
	}
	
	private final void evictAndInsert(T obj) {
		
		this.indexMap.remove(this.array[0].get_id());
		this.indexMap.put(obj.get_id(), 0);
		
		this.array[0] = obj;
		this.heapifyItertiveApproach(0);
	}
	
	public void setArray(T[] array) {
		if(array[0].getClass().equals(UserActivityTimeRange.class)) {
			this.capacity = AppConstants.TIME_RANGE_INTERVALS;
		}
		this.array = array;
	}
	public final T getMin() {
		if(size > 0 && size <= capacity)
			return this.array[0];
		else 
			return null;
	}
	
	public final void updateFrequency(T obj) {
		if(indexMap.containsKey(obj.get_id())) {
			int idx = indexMap.get(obj.get_id());
			this.array[idx] = obj;
			this.heapifyRecursiveApproach(idx);
		}
	}
	
	public final T getMax() {
		for(int i = capacity -1; i >= 0; i--) {
			if(array[i] != null)
				return this.array[i];
		}
		return null;
	}
}
