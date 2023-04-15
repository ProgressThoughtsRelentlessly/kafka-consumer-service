package com.pthore.service.kafkaConsumers.serdes;

import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pthore.service.kafkaConsumers.models.DomainViewCount;
import com.pthore.service.kafkaConsumers.models.AConsumerObject;
import com.pthore.service.kafkaConsumers.models.PostUpvoteCount;
import com.pthore.service.kafkaConsumers.models.PostViewCount;
import com.pthore.service.kafkaConsumers.models.SearchedKeywordCount;
import com.pthore.service.kafkaConsumers.models.UserActivityTimeRange;
import com.pthore.service.kafkaConsumers.models.UserProfileCount;

public class IConsumerObjectValueSerde implements RedisSerializer<AConsumerObject<?>> {
	
	private Logger logger = LoggerFactory.getLogger(IConsumerObjectValueSerde.class);
	
	private JsonMapper mapper;
	
	public IConsumerObjectValueSerde() {
		mapper = new JsonMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
	}
	
	@Override
	public byte[] serialize(AConsumerObject<?> t) throws SerializationException {

		try {
			byte[] bytes = mapper.writeValueAsBytes(t);
			return bytes;
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		
		return new byte[0];
	}

	@Override
	public AConsumerObject<?> deserialize(byte[] bytes) throws SerializationException {
		AConsumerObject<?> result = null;
		String json;
		try {
			JsonNode rootNode = mapper.readTree(bytes);
			Iterator<String> fieldNames  = rootNode.fieldNames();
			String className = fieldNames.next();
			
			JsonNode  classNode = rootNode.elements().next();
			json = classNode.toString(); // .toPrettyString();
			
			switch(className) {
			case "PostViewCount":
				result = mapper.readValue(json, PostViewCount.class);
				break;
			case "DomainViewCount":
				result = mapper.readValue(json, DomainViewCount.class);
				
				break;
			case "PostUpvoteCount":
				result = mapper.readValue(json, PostUpvoteCount.class);
				break;
			case "SearchedKeywordCount":
				result = mapper.readValue(json, SearchedKeywordCount.class);
				break;
			case "UserProfileCount":
				result = mapper.readValue(json, UserProfileCount.class);
				break;
			case "UserActivityTimeRange":
				result = mapper.readValue(json, UserActivityTimeRange.class);
				break;
			default:
				break;
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return result;
	}

}
