package com.hangha.postservice.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String code;    // 에러 코드
    private final String message; // 에러 메시지
    private final int status;     // HTTP 상태 코드

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus().value();
    }
}
