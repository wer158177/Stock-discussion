package com.hangha.activityservice.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlarmDto {

    private Long userId;
    private Long targetId;
    private String content;

    public AlarmDto(Long userId, Long targetId, String content) {
        this.userId = userId;
        this.targetId = targetId;
        this.content = content;
    }

}
