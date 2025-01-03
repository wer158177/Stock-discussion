package com.hangha.activityservice.domain.Service;

import com.hangha.activityservice.controller.AlarmDto;
import com.hangha.activityservice.domain.entity.Alarm;
import com.hangha.activityservice.domain.repository.AlarmRepository;
import com.hangha.activityservice.infrastructure.UserClient;
import com.hangha.activityservice.infrastructure.UserWebClient;
import com.hangha.common.event.model.UserActivityEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import lombok.extern.slf4j.Slf4j;

// 지금 유저 아이디값으로 알람을 보내주기 때문에 유저 이름으로 보내는걸로 변경해야함

@Slf4j
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserClient userClient;
    private final UserWebClient userWebClient;

    // Flux Sink를 사용하여 실시간 알람을 보내기 위한 설정
    private final Sinks.Many<Alarm> alarmSink = Sinks.many().replay().all();

    public AlarmService(AlarmRepository alarmRepository, UserClient userClient, UserWebClient userWebClient) {
        this.alarmRepository = alarmRepository;
        this.userClient = userClient;
        this.userWebClient = userWebClient;
    }

    // 유저 활동에 대한 알람 생성
    public void processUserActivity(UserActivityEvent event) {
        log.info("Processing user activity: Event={}", event);
        String content = generateAlarmContent(event);
        log.info("Generated alarm content: {}", content);

        // 팔로우와 관련된 이벤트 처리
        if ("Follow".equals(event.getActivityType()) || "Unfollow".equals(event.getActivityType())) {
            log.info("Follow activity detected: {}", event);
            processFollowActivity(event, content);
        }

        // 게시글과 댓글 활동 처리
        if ("POST".equals(event.getTargetType())) {
            log.info("Post activity detected: {}", event);
            processPostActivity(event, content);
        } else if ("COMMENT".equals(event.getTargetType())) {
            log.info("Comment activity detected: {}", event);
            processCommentActivity(event, content);
        }
    }

    // 팔로우 활동 처리
    public void processFollowActivity(UserActivityEvent event, String content) {
        Long userId = event.getUserId();
        Long targetUserId = event.getTargetId();
        log.info("Processing follow activity: User {} followed User {}. Content: {}", userId, targetUserId, content);

        // 팔로우된 대상에게 알람을 저장하고 스트리밍으로 전송
        saveAlarm(targetUserId, userId, content);  // 팔로우된 대상에게 알림 저장
        alarmSink.tryEmitNext(new Alarm(targetUserId, userId, content));  // 팔로우된 대상에게 실시간 알람 전송
    }

    // 게시글 활동 처리
    public void processPostActivity(UserActivityEvent event, String content) {
        Long userId = event.getUserId();
        Long postId = event.getTargetId();
        log.info("Processing post activity: User {} posted. Post ID: {}. Content: {}", userId, postId, content);

        // 팔로워들에게 알람을 스트리밍
        Flux<Long> followers = getFollowersInPages(userId);  // Flux로 팔로워 목록을 가져옴
        followers.flatMap(followerId -> {
                    log.info("Sending post activity notification to follower: {}", followerId);
                    saveAlarm(followerId, postId, content);  // 각 팔로워에게 알람 저장
                    alarmSink.tryEmitNext(new Alarm(followerId, postId, content));  // 알람을 스트리밍으로 전송
                    return Flux.empty();  // 알람 전송 후 빈 Flux 반환
                }).switchIfEmpty(Flux.never())  // 데이터가 없으면 스트리밍을 계속 유지
                .subscribe();  // 비동기적으로 팔로워들에게 알람을 전송
    }

    // 댓글 활동 처리
    public void processCommentActivity(UserActivityEvent event, String content) {
        Long userId = event.getUserId();
        Long commentId = event.getTargetId();
        log.info("Processing comment activity: User {} commented. Comment ID: {}. Content: {}", userId, commentId, content);

        // 댓글 활동에 대해 팔로워들에게 알람을 스트리밍
        Flux<Long> followers = getFollowersInPages(userId);  // Flux로 팔로워 목록을 가져옴
        followers.flatMap(followerId -> {
                    log.info("Sending comment activity notification to follower: {}", followerId);
                    saveAlarm(followerId, commentId, content);  // 각 팔로워에게 알람 저장
                    alarmSink.tryEmitNext(new Alarm(followerId, commentId, content));  // 알람을 스트리밍으로 전송
                    return Flux.empty();  // 알람 전송 후 빈 Flux 반환
                }).switchIfEmpty(Flux.never())  // 데이터가 없으면 스트리밍을 계속 유지
                .subscribe();  // 비동기적으로 팔로워들에게 알람을 전송
    }

    // 팔로워 목록을 커서 방식으로 가져오는 메서드 (예시)
    public Flux<Long> getFollowersInPages(Long userId) {
        int size = 1000;  // 한 번에 가져올 팔로워의 수
        Long cursor = 0L;  // 커서 (시작 위치)
        log.info("Fetching followers for userId: {} with cursor: {} and size: {}", userId, cursor, size);

        // userWebClient.getFollowers 호출 (WebClient를 통한 비동기 호출)
        return userWebClient.getFollowers(userId, cursor, size)
                .doOnNext(followerId -> log.info("Fetched follower: {}", followerId))  // 개별 팔로워 ID 로깅
                .doOnComplete(() -> log.info("Completed fetching followers for userId: {}", userId))
                .doOnError(error -> log.error("Error occurred while fetching followers for userId: {}: {}", userId, error.getMessage()));
    }

    // 알람 저장
    public void saveAlarm(Long userId, Long targetId, String content) {
        Alarm alarm = new Alarm(userId, targetId, content);
        log.info("Saving alarm: userId={} targetId={} content={}", userId, targetId, content);
        alarmRepository.save(alarm);  // DB에 알람 저장
    }

    // 알람 메시지 생성
    public String generateAlarmContent(UserActivityEvent event) {
        String content = "";
        log.info("Generating alarm content for event: {}", event);

        switch (event.getActivityType()) {
            case "POST_CREATE":
                content = String.format(AlarmMessage.POST_CREATED, event.getUserId());
                break;
            case "POST_COMMENT":
                content = String.format(AlarmMessage.POST_COMMENTED, event.getUserId());
                break;
            case "POST_LIKE":
                content = String.format(AlarmMessage.POST_LIKED, event.getUserId());
                break;
            case "Comment_CREATE":
                content = String.format(AlarmMessage.COMMENT_CREATED, event.getUserId());
                break;
            case "COMMENT_COMMENT":
                content = String.format(AlarmMessage.COMMENT_COMMENTED, event.getUserId());
                break;
            case "COMMENT_LIKE":
                content = String.format(AlarmMessage.COMMENT_LIKED, event.getUserId());
                break;
            case "Follow":
                content = String.format(AlarmMessage.FOLLOWED, event.getUserId(), event.getTargetId());
                break;
            case "Unfollow":
                content = String.format(AlarmMessage.UNFOLLOWED, event.getUserId(), event.getTargetId());
                break;
            case "FOLLOWING_POST_CREATE":
                content = String.format(AlarmMessage.FOLLOWING_POST_CREATED, event.getUserId());
                break;
            case "FOLLOWING_COMMENT_CREATE":
                content = String.format(AlarmMessage.FOLLOWING_COMMENT_CREATED, event.getUserId());
                break;
            default:
                break;
        }

        log.info("Generated alarm content: {}", content);
        return content;
    }

    public Flux<AlarmDto> sendRealTimeAlarms(Long userId) {
        log.info("Sending real-time alarms for userId: {}", userId);
        return alarmSink.asFlux()
                .filter(alarm -> alarm.getUserId().equals(userId)) // 필터링된 알람을 전달
                .map(alarm -> new AlarmDto(alarm.getUserId(), alarm.getTargetId(), alarm.getContent())); // Alarm -> AlarmDto 변환
    }
}

