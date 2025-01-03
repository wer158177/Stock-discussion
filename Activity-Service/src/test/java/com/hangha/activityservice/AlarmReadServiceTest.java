package com.hangha.activityservice;

import com.hangha.activityservice.domain.Service.AlarmReadService;
import com.hangha.activityservice.domain.entity.Alarm;
import com.hangha.activityservice.domain.repository.AlarmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AlarmReadServiceTest {

    @Mock
    private AlarmRepository alarmRepository;  // AlarmRepository Mock 객체

    @InjectMocks
    private AlarmReadService alarmReadService;  // 실제 서비스 객체

    private Long userId;
    private Alarm alarm1;
    private Alarm alarm2;

    @BeforeEach
    void setUp() {
        userId = 1L;
        alarm1 = new Alarm(userId, 100L, "Test alarm 1");
        alarm2 = new Alarm(userId, 101L, "Test alarm 2");

        alarm1.markAsRead();  // alarm1은 이미 읽음 상태
    }

    @Test
    void testGetAllAlarms() {
        // Given: 모든 알람을 반환하도록 설정
        when(alarmRepository.findByUserId(userId)).thenReturn(Arrays.asList(alarm1, alarm2));

        // When: 서비스 메서드 실행
        System.out.println("Running testGetAllAlarms...");
        var alarms = alarmReadService.getAllAlarms(userId);
        System.out.println("Retrieved alarms: " + alarms);

        // Then: 알람 목록이 제대로 반환되는지 검증
        assertNotNull(alarms);
        assertEquals(2, alarms.size());  // 알람 2개가 반환되어야 한다.
        verify(alarmRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetUnreadAlarms() {
        // Given: 읽지 않은 알람만 반환하도록 설정
        when(alarmRepository.findByUserIdAndReadStatus(userId, false)).thenReturn(Arrays.asList(alarm2));

        // When: 서비스 메서드 실행
        System.out.println("Running testGetUnreadAlarms...");
        var unreadAlarms = alarmReadService.getUnreadAlarms(userId);
        System.out.println("Retrieved unread alarms: " + unreadAlarms);

        // Then: 읽지 않은 알람이 제대로 반환되는지 검증
        assertNotNull(unreadAlarms);
        assertEquals(1, unreadAlarms.size());  // 읽지 않은 알람은 1개여야 한다.
        assertEquals("Test alarm 2", unreadAlarms.get(0).getContent());
        verify(alarmRepository, times(1)).findByUserIdAndReadStatus(userId, false);
    }

    @Test
    void testMarkAlarmAsRead() {
        // Given: 알람 ID로 알람을 찾아서 읽음 처리할 수 있도록 설정
        when(alarmRepository.findById(alarm1.getId())).thenReturn(Optional.of(alarm1));

        // When: 알람을 읽음 처리
        System.out.println("Running testMarkAlarmAsRead...");
        alarmReadService.markAlarmAsRead(alarm1.getId());
        System.out.println("Processed alarm: " + alarm1);

        // Then: 알람이 읽음 상태로 변경되었는지 검증
        assertTrue(alarm1.isReadStatus());  // 알람이 읽음 상태로 바뀌었어야 한다.
        verify(alarmRepository, times(1)).findById(alarm1.getId());
        verify(alarmRepository, times(1)).save(alarm1);  // 읽음 처리 후 저장이 호출되어야 한다.
    }

    @Test
    void testMarkAlarmAsRead_AlarmNotFound() {
        // Given: 존재하지 않는 알람 ID
        when(alarmRepository.findById(999L)).thenReturn(Optional.empty());

        // When + Then: 알람을 찾을 수 없는 경우 예외가 발생하는지 검증
        System.out.println("Running testMarkAlarmAsRead_AlarmNotFound...");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            alarmReadService.markAlarmAsRead(999L);
        });

        System.out.println("Caught exception: " + exception.getMessage());
        assertEquals("알람을 찾을 수 없습니다.", exception.getMessage());
        verify(alarmRepository, times(1)).findById(999L);
    }
}
