package com.hangha.common.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class UserActivityEvent {

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("activityType")
    private String activityType;

    @JsonProperty("targetId")
    private Long targetId;

    @JsonProperty("targetType")
    private String targetType;

    @JsonProperty("metadata")
    @JsonSerialize
    private Map<String, Object> metadata;


    public UserActivityEvent(Long userId, String activityType, Long targetId, String targetType, Map<String, Object> metadata) {
        this.userId = userId;
        this.activityType = activityType;
        this.targetId = targetId;
        this.targetType = targetType;
        this.metadata = metadata;
    }
}
