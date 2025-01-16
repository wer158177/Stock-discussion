package com.hangha.livechatservice.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hangha.livechatservice.infrastructure.dto.ChatMessageResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class MessageBroadcaster {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(MessageBroadcaster.class);

    public MessageBroadcaster(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.objectMapper = new ObjectMapper();
    }

//    public Mono<Void> broadcast(WebSocketSession senderSession, ChatMessageResponseDto responseDto) {
//        String roomName = responseDto.getRoomName();
//        String broadcastMessage = getSerializedMessage(responseDto); // 캐싱된 직렬화 메시지
//
//        return sessionManager.getSessions(roomName)
//                .filter(session -> session.isOpen() && !session.equals(senderSession))
//                .onBackpressureLatest() // 최신 메시지 우선 처리
//                .flatMap(session -> session.send(Mono.just(session.textMessage(broadcastMessage)))
//                        .doOnError(error -> handleFailedSession(session, error)), 10) // 동시성 10
//                .then();
//    }


    public Mono<Void> broadcast(WebSocketSession senderSession, ChatMessageResponseDto responseDto) {
        String roomName = responseDto.getRoomName();
        String broadcastMessage = getSerializedMessage(responseDto);

        return sessionManager.getSessions(roomName)
                .filter(session -> session.isOpen() && !session.getId().equals(senderSession.getId())) // 본인을 제외한 세션 필터링
                .parallel()
                .runOn(Schedulers.parallel()) // 병렬 처리
                .flatMap(session -> session.send(Mono.just(session.textMessage(broadcastMessage)))
                        .doOnError(error -> handleFailedSession(session, error)))
                .sequential() // 병렬 처리가 끝나면 순차적으로 결합
                .then();
    }



    private String getSerializedMessage(ChatMessageResponseDto responseDto) {
        try {
            return objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("메시지 직렬화 실패", e);
        }
    }

    private void handleFailedSession(WebSocketSession session, Throwable error) {
        logger.warn("세션 {}에 메시지 전송 실패: {}", session.getId(), error.getMessage());
        sessionManager.removeUserFromRoom(session)
                .doOnSuccess(unused -> logger.info("끊어진 세션 {}이 제거되었습니다.", session.getId()))
                .doOnError(removalError -> logger.error("세션 {} 제거 실패: {}", session.getId(), removalError.getMessage()))
                .subscribe();
    }












//    public Mono<Void> broadcastMessage(WebSocketSession senderSession, ChatMessageResponseDto responseDto) {
//        String roomName = responseDto.getRoomName();
//
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            String broadcastMessage = objectMapper.writeValueAsString(responseDto);
//
//            return sessionManager.getSessions(roomName)
//                    .filter(session -> !session.getId().equals(senderSession.getId())) // 본인을 제외한 세션만 필터링
//                    .flatMap(session -> session.send(Mono.just(session.textMessage(broadcastMessage)))
//                            .doOnError(error -> {
//                                logger.warn("세션 {}에 메시지 전송 실패: {}", session.getId(), error.getMessage());
//                                // 전송 실패한 세션을 즉시 제거
//                                sessionManager.removeUserFromRoom(session);
//                                logger.info("끊어진 세션 {}이 제거되었습니다.", session.getId());
//                            }))
//                    .then();
//        } catch (JsonProcessingException e) {
//            logger.error("메시지 직렬화 실패: {}", e.getMessage(), e);
//            return Mono.error(new RuntimeException("메시지 직렬화에 실패했습니다.", e));
//        } catch (Exception e) {
//            logger.error("브로드캐스트 중 알 수 없는 에러 발생: {}", e.getMessage(), e);
//            return Mono.error(new RuntimeException("브로드캐스트 중 알 수 없는 오류가 발생했습니다.", e));
//        }
//    }





}

