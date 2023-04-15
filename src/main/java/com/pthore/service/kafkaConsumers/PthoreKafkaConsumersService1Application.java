package com.pthore.service.kafkaConsumers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@SpringBootApplication
@EnableScheduling
public class PthoreKafkaConsumersService1Application { // implements ApplicationListener<ApplicationReadyEvent> , DisposableBean {

//	@Autowired
//	private KafkaConsumerService kafkaConsumerService;
	
	public static void main(String[] args) {
		
		SpringApplication.run(PthoreKafkaConsumersService1Application.class, args);
		
	}
	
	@Bean
	@Primary
	public ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper()
					.registerModule(new ParameterNamesModule())
				    .registerModule(new Jdk8Module())
				    .registerModule(new JavaTimeModule());;
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
		return mapper;
	}
	
//	@Override
//	public void onApplicationEvent(ApplicationReadyEvent event) {
//		
//		Thread kafkaConsumerServiceStartThread = new Thread(() -> {
//			kafkaConsumerService.pollRecordsFromAllRegisteredTopics();
//		});
//		kafkaConsumerServiceStartThread.setName("kafka-consumer-start-thread");
//		kafkaConsumerServiceStartThread.setDaemon(true);
//		kafkaConsumerServiceStartThread.setPriority(Thread.MAX_PRIORITY);
//		kafkaConsumerServiceStartThread.start();
//		
//	}


//	@Override
//	public void destroy() throws Exception {
//		
//		
//	}

}
