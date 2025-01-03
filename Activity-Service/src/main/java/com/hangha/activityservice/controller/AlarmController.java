package com.hangha.activityservice.controller;



import com.hangha.activityservice.domain.Service.AlarmReadService;
import com.hangha.activityservice.domain.Service.AlarmService;
import com.hangha.activityservice.domain.entity.Alarm;
import com.hangha.common.event.model.UserActivityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/alarms")
public class AlarmController {

    private final AlarmReadService alarmReadService;
    private final AlarmService alarmService;
    public AlarmController(AlarmReadService alarmReadService, AlarmService alarmService) {
        this.alarmReadService = alarmReadService;
        this.alarmService = alarmService;
    }

    // 사용자의 모든 알람 조회
    @GetMapping
    public ResponseEntity<List<Alarm>> getAllAlarms(@RequestParam Long userId) {
        List<Alarm> alarms = alarmReadService.getAllAlarms(userId);
        return ResponseEntity.ok(alarms);
    }

    // 사용자의 읽지 않은 알람만 조회
    @GetMapping("/unread")
    public ResponseEntity<List<Alarm>> getUnreadAlarms(@RequestParam Long userId) {
        List<Alarm> unreadAlarms = alarmReadService.getUnreadAlarms(userId);
        return ResponseEntity.ok(unreadAlarms);
    }

    // 특정 알람 읽음 처리
    @PatchMapping("/{alarmId}/read")
    public ResponseEntity<Void> markAlarmAsRead(@PathVariable Long alarmId) {
        alarmReadService.markAlarmAsRead(alarmId);
        return ResponseEntity.noContent().build();  // No content response
    }


    @GetMapping(value = "/alarms/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AlarmDto> getRealTimeAlarms(@PathVariable Long userId) {
        return alarmService.sendRealTimeAlarms(userId)
                .doOnTerminate(() -> log.info("SSE stream terminated for userId: {}", userId));  // 스트리밍 종료 시 로그 출력
    }


}
