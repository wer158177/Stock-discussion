package com.hangha.activityservice.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hangha.activityservice.application.UserActivityApplicationService;
import com.hangha.activityservice.domain.repository.UserActivityRepository;
import com.hangha.common.event.model.UserActivityEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserActivityConsumer {

    private final UserActivityRepository logRepository;
    private final ObjectMapper objectMapper; // ObjectMapper를 사용하여 JSON을 객체로 변환
    private final UserActivityApplicationService userActivityApplicationService;

    public UserActivityConsumer(UserActivityRepository logRepository, ObjectMapper objectMapper, UserActivityApplicationService userActivityApplicationService) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
        this.userActivityApplicationService = userActivityApplicationService;
    }

    @KafkaListener(topics = "user-activity-topic", groupId = "user-activity-log-group")
    public void consume(String eventJson) {
        try {
            // String 메시지를 UserActivityEvent로 변환
            UserActivityEvent event = objectMapper.readValue(eventJson, UserActivityEvent.class);
            System.out.println("Consumed event: " + event.toString());


            userActivityApplicationService.handleUserActivity(event);


        } catch (IllegalArgumentException e) {
            System.err.println("Invalid targetType: " + eventJson);
            e.printStackTrace(); // 예외의 스택 트레이스를 로그로 출력
        } catch (Exception e) {
            System.err.println("Error processing event: " + eventJson);
            e.printStackTrace(); // 다른 예외에 대한 로그
        }
    }
}
