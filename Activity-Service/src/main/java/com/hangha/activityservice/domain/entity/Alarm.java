package com.hangha.activityservice.domain.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long targetId;


    private String content;

    private boolean readStatus = false;

    private  LocalDateTime created_at =LocalDateTime.now();

    @Builder
    public Alarm(Long userId, Long targetId, String content) {
        this.userId = userId;
        this.targetId = targetId;
        this.content = content;
    }

    public void markAsRead() {
        this.readStatus = true;
    }


    // equals와 hashCode 메서드 추가
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return userId.equals(alarm.userId) &&
                targetId.equals(alarm.targetId) &&
                content.equals(alarm.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, targetId, content);
    }

}
