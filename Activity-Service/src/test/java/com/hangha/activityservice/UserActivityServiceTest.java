package com.hangha.activityservice;

import com.hangha.activityservice.domain.Service.UserActivityService;
import com.hangha.activityservice.domain.entity.TargetType;
import com.hangha.activityservice.domain.entity.UserActivityLog;
import com.hangha.activityservice.domain.repository.UserActivityRepository;
import com.hangha.common.event.model.UserActivityEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserActivityServiceTest {

    @Mock
    private UserActivityRepository userActivityRepository;  // UserActivityRepository의 Mock 객체

    @InjectMocks
    private UserActivityService userActivityService;  // 실제 서비스 객체

    @BeforeEach
    void setUp() {
        // 각 테스트 실행 전 공통적으로 필요한 설정을 할 수 있습니다.
    }

    @Test
    void testProcessUserActivity() {
        // Given: 테스트를 위한 가짜 UserActivityEvent 생성
        Long userId = 1L;
        Long targetId = 100L;
        String activityType = "POST_CREATE";
        String targetType = "POST";  // TargetType으로 변환될 값
        UserActivityEvent event = new UserActivityEvent(userId, activityType, targetId, targetType, null);

        // When: 서비스 메서드 실행
        System.out.println("Processing User Activity: UserId=" + userId + ", ActivityType=" + activityType + ", TargetId=" + targetId + ", TargetType=" + targetType);
        userActivityService.processUserActivity(event);

        // Then: UserActivityRepository의 save 메서드가 한 번 호출되었는지 확인
        verify(userActivityRepository, times(1)).save(any(UserActivityLog.class));

        // 확인: log 객체가 제대로 생성되고 targetType이 POST로 설정되었는지 확인
        verify(userActivityRepository).save(argThat(log -> {
            // 로그 값 출력
            System.out.println("Log Saved: UserId=" + log.getUserId() + ", ActivityType=" + log.getActivityType() + ", TargetId=" + log.getTargetId() + ", TargetType=" + log.getTargetType());
            return log.getUserId().equals(userId) &&
                    log.getActivityType().equals(activityType) &&
                    log.getTargetId().equals(targetId) &&
                    log.getTargetType() == TargetType.POST; // TargetType이 제대로 설정되었는지 확인
        }));
    }
}
