package com.hangha.activityservice.application;

import com.hangha.activityservice.domain.Service.AlarmService;
import com.hangha.activityservice.domain.Service.UserActivityService;
import com.hangha.common.event.model.UserActivityEvent;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserActivityApplicationService {

    private final UserActivityService userActivityService;  // 유저 활동 서비스
    private final AlarmService alarmService;  // 알람 서비스

    public UserActivityApplicationService(UserActivityService userActivityService, AlarmService alarmService) {
        this.userActivityService = userActivityService;
        this.alarmService = alarmService;
    }

    // 유저 활동 이벤트를 처리
    @Transactional
    public void handleUserActivity(UserActivityEvent event) {
        // 유저 활동 처리 (게시글 작성, 댓글 작성, 좋아요 등)
        userActivityService.processUserActivity(event);
        alarmService.processUserActivity(event);
    }
}



