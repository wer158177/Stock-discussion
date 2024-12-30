package com.hangha.activityservice.domain.entity;


import com.netflix.discovery.converters.Converters;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
public class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 로그 고유 ID

    @Column(nullable = false)
    private Long userId; // 활동을 수행한 사용자 ID

    @Column(nullable = false, length = 50)
    private String activityType; // 활동 유형 (예: POST_CREATE, LIKE_POST 등)

    @Column
    private Long targetId; // 대상 ID (게시글 ID, 댓글 ID 등)

    @Enumerated(EnumType.STRING)  // enum 값을 문자열로 저장
    @Column(nullable = false)
    private TargetType targetType; // 대상 유형 (예: POST, COMMENT, USER 등)

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 활동 발생 시간

    @Convert(converter = MetadataConverter.class)
    @Column(columnDefinition = "JSON")
    private Map<String, Object> metadata; // 추가 메타데이터 (JSON 형식)

    @Builder
    public UserActivityLog(Long userId, String activityType, Long targetId, TargetType targetType, Map<String, Object> metadata) {
        this.userId = userId;
        this.activityType = activityType;
        this.targetId = targetId;
        this.targetType = targetType;
        this.metadata = metadata;
    }




}
