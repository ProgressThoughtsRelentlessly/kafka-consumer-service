package com.pthore.service.kafkaConsumers.models;

public abstract class AConsumerObject<T> implements Comparable<T>{
	
	public String _id;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
	
}
