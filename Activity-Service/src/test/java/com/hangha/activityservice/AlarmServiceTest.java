package com.hangha.activityservice;

import com.hangha.activityservice.controller.AlarmDto;
import com.hangha.activityservice.domain.Service.AlarmMessage;
import com.hangha.activityservice.domain.Service.AlarmService;
import com.hangha.activityservice.domain.entity.Alarm;
import com.hangha.activityservice.domain.repository.AlarmRepository;
import com.hangha.activityservice.infrastructure.UserClient;
import com.hangha.common.event.model.UserActivityEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private AlarmService alarmService;

    @BeforeEach
    void setUp() {
        // Set up any pre-test configurations
    }

    @Test
    void testSaveAlarm() {
        // Given
        Long userId = 1L;
        Long targetId = 2L;
        String content = "Test content";

        Alarm alarm = new Alarm(userId, targetId, content);

        // When
        alarmService.saveAlarm(userId, targetId, content);

        // Then
        // 실제로 전달된 Alarm 객체의 값을 확인
        System.out.println("Verifying saveAlarm method was called...");
        System.out.println("Alarm to be saved: UserId=" + alarm.getUserId() + ", TargetId=" + alarm.getTargetId() + ", Content=" + alarm.getContent());
        verify(alarmRepository, times(1)).save(eq(alarm));  // Verify that save is called with correct Alarm
    }


    @Test
    void testProcessPostActivity() {
        // Given
        Long userId = 1L;
        Long postId = 100L;
        String content = "Test post content";
        UserActivityEvent event = new UserActivityEvent(userId, "POST_CREATE", postId, "POST", null);

        // Mock the getFollowersInPages method to return a Flux of followers
        Flux<Long> mockFollowers = Flux.just(1L, 2L, 3L);  // Mock three followers
        when(userClient.getFollowers(userId, 0L, 1000)).thenReturn(mockFollowers);

        // When
        alarmService.processPostActivity(event, content);  // Process the post activity

        // Then
        // Use ArgumentCaptor to capture the arguments passed to the save method
        ArgumentCaptor<Alarm> captor = ArgumentCaptor.forClass(Alarm.class);
        verify(alarmRepository, times(3)).save(captor.capture());  // Verify that save is called 3 times

        // Capture and print each Alarm saved
        captor.getAllValues().forEach(alarm -> {
            System.out.println("Captured Alarm: UserId=" + alarm.getUserId() + ", TargetId=" + alarm.getTargetId() + ", Content=" + alarm.getContent());
        });
    }







    @Test
    void testSendRealTimeAlarms() {
        // Given
        Long userId = 1L;
        Long postId = 100L;
        String content = "Test post content";

        // Create mock for followers
        Flux<Long> mockFollowers = Flux.just(1L, 2L, 3L); // Simulating three followers

        // Mocking the userClient to return mockFollowers
        when(userClient.getFollowers(userId, 0L, 1000)).thenReturn(mockFollowers);

        // Trigger the post activity
        UserActivityEvent event = new UserActivityEvent(userId, "POST_CREATE", postId, "POST", null);
        alarmService.processPostActivity(event, content);

        // When
        Flux<AlarmDto> result = alarmService.sendRealTimeAlarms(userId);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(alarmDto -> {
                    // 로그를 찍고 검증할 값을 확인합니다.
                    System.out.println("Received alarm: UserId=" + alarmDto.getUserId() + ", Content=" + alarmDto.getContent());

                    // 알람 내용이 기대하는 값과 일치하는지 확인
                    return alarmDto.getUserId().equals(userId) && alarmDto.getContent().equals(content);
                })
                .expectComplete() // Expect completion
                .verify(); // Trigger verification
    }




    @Test
    void testGeneratePostCreateContent() {
        // Given
        Long userId = 1L;
        String expectedContent = String.format(AlarmMessage.POST_CREATED, userId);
        UserActivityEvent event = new UserActivityEvent(userId, "POST_CREATE", 100L, "POST", null); // "POST_CREATE" 이벤트 생성

        // When
        String generatedContent = alarmService.generateAlarmContent(event);  // generateAlarmContent 호출

        // Then
        System.out.println("Generated content for POST_CREATE: " + generatedContent);  // 로그로 출력된 내용 확인
        assertEquals(expectedContent, generatedContent);  // 생성된 메시지가 예상한 메시지와 일치하는지 확인
    }







}
