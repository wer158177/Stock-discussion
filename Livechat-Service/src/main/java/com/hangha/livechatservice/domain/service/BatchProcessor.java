package com.hangha.livechatservice.domain.service;

import com.hangha.livechatservice.domain.entity.ChatMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@Component
public class BatchProcessor {

    // 클래스 내에 추가
    private static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);
    private final Sinks.Many<ChatMessage> messageSink = Sinks.many().unicast().onBackpressureBuffer();
    private final Queue<ChatMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private static final int BATCH_SIZE = 1000;
    private static final Duration FLUSH_INTERVAL = Duration.ofSeconds(1);
    private final DatabaseClient databaseClient;

    public BatchProcessor(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initBatchFlush() {
        startBatchFlush();
    }

    public void startBatchFlush() {
        Flux.interval(FLUSH_INTERVAL)
                .flatMap(tick -> flushMessagesToDatabase())
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }



    // flushMessagesToDatabase 메서드 개선
    private Mono<Void> flushMessagesToDatabase() {
        return Mono.defer(() -> {
            List<ChatMessage> batch = new ArrayList<>();
            while (!messageQueue.isEmpty() && batch.size() < BATCH_SIZE) {
                ChatMessage message = messageQueue.poll();
                if (message != null) {
                    batch.add(message);
                }
            }

            if (batch.isEmpty()) {
                return Mono.empty();
            }

            StringBuilder sqlBuilder = new StringBuilder(
                    "INSERT INTO chat_message (chat_room_name, content, created_at, sender_name, sender_profile_url) VALUES ");
            for (int i = 0; i < batch.size(); i++) {
                sqlBuilder.append("(:chatRoomName").append(i)
                        .append(", :content").append(i)
                        .append(", :createdAt").append(i)
                        .append(", :senderName").append(i)
                        .append(", :senderProfileUrl").append(i)
                        .append(")");
                if (i < batch.size() - 1) {
                    sqlBuilder.append(", ");
                }
            }

            DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(sqlBuilder.toString());

            for (int i = 0; i < batch.size(); i++) {
                ChatMessage message = batch.get(i);
                executeSpec = executeSpec
                        .bind("chatRoomName" + i, message.getChatRoomName())
                        .bind("content" + i, message.getContent())
                        .bind("createdAt" + i, message.getCreatedAt())
                        .bind("senderName" + i, message.getSenderName());

                if (message.getSenderProfileUrl() != null) {
                    executeSpec = executeSpec.bind("senderProfileUrl" + i, message.getSenderProfileUrl());
                } else {
                    executeSpec = executeSpec.bindNull("senderProfileUrl" + i, String.class);
                }
            }

            return executeSpec.fetch()
                    .rowsUpdated()
                    .doOnError(e -> {
                        // 로그 출력 추가
                        logger.error("Failed to save batch to database. Re-adding messages to the queue.", e);
                        batch.forEach(msg -> logger.error("Failed Message: {}", msg));
                        // 실패한 메시지를 큐에 다시 추가
                        messageQueue.addAll(batch);
                    })
                    .then();
        });
    }

    public void addMessageToQueue(ChatMessage message) {
        messageSink.tryEmitNext(message);
    }

    @PostConstruct
    public void initMessageSinkSubscriber() {
        messageSink.asFlux()
                .doOnNext(messageQueue::add)
                .subscribe();
    }
}