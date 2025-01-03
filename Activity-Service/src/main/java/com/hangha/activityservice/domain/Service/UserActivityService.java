package com.hangha.activityservice.domain.Service;

import com.hangha.activityservice.domain.entity.TargetType;
import com.hangha.activityservice.domain.entity.UserActivityLog;
import com.hangha.activityservice.domain.repository.UserActivityRepository;
import com.hangha.common.event.model.UserActivityEvent;

import org.springframework.stereotype.Service;

@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    // 유저 활동 이벤트 처리
    public void processUserActivity(UserActivityEvent event) {
        // 이벤트에서 targetType을 받아서 Enum으로 변환
        TargetType targetType = TargetType.valueOf(event.getTargetType());

        System.out.println("Converted targetType: " + targetType); // Enum 변환 후 로그

        // UserActivityLog 객체 생성 (빌더 패턴 사용)
        UserActivityLog log = UserActivityLog.builder()
                .userId(event.getUserId())
                .activityType(event.getActivityType())
                .targetId(event.getTargetId())
                .targetType(targetType)
                .metadata(event.getMetadata())
                .build();

        // DB에 로그 저장
        userActivityRepository.save(log);
        System.out.println("Log saved: " + log);  // 정상적인 로그 출력
    }
}


