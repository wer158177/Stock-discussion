package com.hangha.activityservice.domain.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.activityservice.domain.entity.TargetType;
import com.hangha.activityservice.domain.entity.UserActivityLog;
import com.hangha.activityservice.domain.repository.UserActivityRepository;
import com.hangha.common.event.model.UserActivityEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserActivityConsumer {

    private final UserActivityRepository logRepository;
    private final ObjectMapper objectMapper; // ObjectMapper를 사용하여 JSON을 객체로 변환

    public UserActivityConsumer(UserActivityRepository logRepository, ObjectMapper objectMapper) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-activity-topic", groupId = "user-activity-log-group")
    public void consume(String eventJson) {
        try {
            // String 메시지를 UserActivityEvent로 변환
            UserActivityEvent event = objectMapper.readValue(eventJson, UserActivityEvent.class);
            System.out.println("Consumed event: " + event.toString());

            // Enum 변환
            TargetType targetType = TargetType.valueOf(event.getTargetType());
            System.out.println("Converted targetType: " + targetType); // Enum 변환 후 로그

            // 로그 생성
            UserActivityLog log = UserActivityLog.builder()
                    .userId(event.getUserId())
                    .activityType(event.getActivityType())
                    .targetId(event.getTargetId())
                    .targetType(targetType)
                    .metadata(event.getMetadata())
                    .build();

            // DB 저장
            logRepository.save(log);
            System.out.println("Log saved: " + log);  // 정상적인 로그 출력

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid targetType: " + eventJson);
            e.printStackTrace(); // 예외의 스택 트레이스를 로그로 출력
        } catch (Exception e) {
            System.err.println("Error processing event: " + eventJson);
            e.printStackTrace(); // 다른 예외에 대한 로그
        }
    }
}
