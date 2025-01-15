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
@Slf4j
public class BatchProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BatchProcessor.class);
    private static final int BATCH_SIZE = 1500;
    private final Queue<ChatMessage> messageQueue = new ConcurrentLinkedQueue<>();
    private final DatabaseClient databaseClient;
    private final Object queueLock = new Object(); // 동기화용 객체

    public BatchProcessor(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }


    /**
     * 메시지를 큐에 추가하고 조건에 따라 배치 작업 실행
     */
    public void enqueueMessage(ChatMessage message) {
        synchronized (queueLock) {
            messageQueue.add(message);

            // 배치 크기 조건 충족 시 즉시 처리
            if (messageQueue.size() >= BATCH_SIZE) {
                processMessageBatch().subscribe();
            }
        }
    }

    /**
     * 메시지 배치 처리
     */
    private Mono<Void> processMessageBatch() {
        return Mono.defer(() -> {
            List<ChatMessage> batch;

            // 큐에서 메시지 추출
            synchronized (queueLock) {
                batch = new ArrayList<>();
                while (!messageQueue.isEmpty() && batch.size() < BATCH_SIZE) {
                    ChatMessage message = messageQueue.poll();
                    if (message != null) {
                        batch.add(message);
                    }
                }
            }

            if (batch.isEmpty()) {
                logger.debug("No messages to process.");
                return Mono.empty();
            }

            logger.info("Processing batch of {} messages.", batch.size());

            String sql = createBatchInsertSql(batch);
            DatabaseClient.GenericExecuteSpec executeSpec = bindBatchParameters(sql, batch);

            return executeSpec.fetch().rowsUpdated()
                    .doOnSuccess(rows -> logger.info("Successfully saved {} messages.", rows))
                    .doOnError(error -> handleBatchFailure(batch, error))
                    .then();
        });
    }

    private String createBatchInsertSql(List<ChatMessage> batch) {
        StringBuilder sqlBuilder = new StringBuilder(
                "INSERT INTO chat_message (chat_room_name, content, created_at, sender_name, sender_profile_url) VALUES ");
        for (int i = 0; i < batch.size(); i++) {
            sqlBuilder.append("(:chatRoomName").append(i)
                    .append(", :content").append(i)
                    .append(", :createdAt").append(i)
                    .append(", :senderName").append(i)
                    .append(", :senderProfileUrl").append(i).append(")");
            if (i < batch.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
        return sqlBuilder.toString();
    }

    private DatabaseClient.GenericExecuteSpec bindBatchParameters(String sql, List<ChatMessage> batch) {
        DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(sql);
        for (int i = 0; i < batch.size(); i++) {
            ChatMessage message = batch.get(i);
            executeSpec = executeSpec.bind("chatRoomName" + i, message.getChatRoomName())
                    .bind("content" + i, message.getContent())
                    .bind("createdAt" + i, message.getCreatedAt())
                    .bind("senderName" + i, message.getSenderName());

            if (message.getSenderProfileUrl() != null) {
                executeSpec = executeSpec.bind("senderProfileUrl" + i, message.getSenderProfileUrl());
            } else {
                executeSpec = executeSpec.bindNull("senderProfileUrl" + i, String.class);
            }
        }
        return executeSpec;
    }

    private void handleBatchFailure(List<ChatMessage> batch, Throwable error) {
        logger.error("Failed to save batch to the database. Requeuing messages.", error);
        synchronized (queueLock) {
            messageQueue.addAll(batch);
        }
    }
}


