package com.hangha.mvclivechatservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync  // @Async 어노테이션을 활성화하는 설정
public class AsyncConfig {
    // 비동기 작업 관련 추가 설정을 할 수 있습니다.


    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);  // 기본 스레드 수
        executor.setMaxPoolSize(20);   // 최대 스레드 수
        executor.setQueueCapacity(100); // 대기 큐의 크기
        executor.setThreadNamePrefix("Async-"); // 스레드 이름 접두어
        executor.initialize();
        return executor;
    }
}
