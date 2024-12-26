package com.hangha.postservice.controller.dto;

import lombok.Getter;


public record PostStatusResponse(int likesCount, int commentsCount, int viewsCount) {



}
