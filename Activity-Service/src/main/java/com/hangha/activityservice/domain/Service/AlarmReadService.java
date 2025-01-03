package com.hangha.activityservice.domain.Service;

import com.hangha.activityservice.domain.entity.Alarm;
import com.hangha.activityservice.domain.repository.AlarmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmReadService {

    private final AlarmRepository alarmRepository;

    public AlarmReadService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    // 사용자의 모든 알람 조회
    public List<Alarm> getAllAlarms(Long userId) {
        return alarmRepository.findByUserId(userId);
    }

    // 사용자의 읽지 않은 알람만 조회
    public List<Alarm> getUnreadAlarms(Long userId) {
        return alarmRepository.findByUserIdAndReadStatus(userId,false );
    }

    // 알람 읽음 처리
    public void markAlarmAsRead(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("알람을 찾을 수 없습니다."));

        alarm.markAsRead();  // 읽음 처리
        alarmRepository.save(alarm);  // DB에 저장
    }
}
