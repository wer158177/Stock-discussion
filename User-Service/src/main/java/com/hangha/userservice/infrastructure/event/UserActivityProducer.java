package com.hangha.userservice.infrastructure.event;

import com.hangha.common.event.model.UserActivityEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserActivityProducer {

    private final KafkaTemplate<String, UserActivityEvent> kafkaTemplate; // UserActivityEvent로 타입 변경
    private final RetryTemplate retryTemplate;

    public UserActivityProducer(KafkaTemplate<String, UserActivityEvent> kafkaTemplate, RetryTemplate retryTemplate) {
        this.kafkaTemplate = kafkaTemplate;

        this.retryTemplate = retryTemplate;
    }

    public void sendActivityEvent(UserActivityEvent event) {
        try {
            // KafkaTemplate이 자동으로 객체를 JSON으로 직렬화하여 전송
            retryTemplate.execute(context -> {
                kafkaTemplate.send("user-activity-topic", event); // UserActivityEvent 객체 전송
                System.out.println("Produced event: " + event); // 직렬화된 JSON 출력 (자동 처리됨)
                return null;
            });
        } catch (Exception e) {
            System.err.println("Failed to produce Kafka event after retries: " + e.getMessage());
            e.printStackTrace();
            // 실패 처리 로직 추가 가능
        }
    }
}