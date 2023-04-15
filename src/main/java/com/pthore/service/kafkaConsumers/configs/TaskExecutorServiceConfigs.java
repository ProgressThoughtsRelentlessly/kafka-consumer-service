package com.pthore.service.kafkaConsumers.configs;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/*
	note:
		reference:
			. https://www.baeldung.com/spring-scheduled-tasks
			. https://www.baeldung.com/spring-async
		@Scheduled 
			. the scheduled method doesn't run in multiple threads by default.
			 only after the previous scheduled execution is complete the next cycle starts
			 else it waits until completion.
			. only when its annotated 
		and 
		@Async:
			. the combination of the above will help run the scheduled 'method' in parallel in case
			 the operation stretches beyond the next schedule time.
*/
@Configuration
@EnableAsync
public class TaskExecutorServiceConfigs implements AsyncConfigurer {
	
	// NOTE: This does not need to be beaned when implementing AsyncConfigurer.
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executors = new ThreadPoolTaskExecutor();
		executors.setCorePoolSize(4);
		executors.setMaxPoolSize(8);
		executors.setDaemon(true);
		executors.setQueueCapacity(16);
		executors.setThreadNamePrefix("Thread-Pool-Task-Executor");
		executors.setThreadPriority(Thread.MAX_PRIORITY);
		executors.initialize();
		return executors;
	}

	@Override
	public Executor getAsyncExecutor() {
		return threadPoolTaskExecutor();
	}
	
	
}
