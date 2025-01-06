package com.hangha.postservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 유효성 검증 관련 에러
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E001", "입력값이 유효하지 않습니다"),
    EMPTY_TAG_LIST(HttpStatus.BAD_REQUEST, "E002", "태그 이름 리스트가 비어 있습니다"),
    INVALID_POST_ID(HttpStatus.BAD_REQUEST, "E003", "게시글 ID가 유효하지 않습니다"),

    // 데이터베이스 관련 에러
    TAG_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E004", "태그 저장에 실패했습니다"),
    POST_TAG_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E005", "태그-게시글 관계 저장에 실패했습니다"),

    // 리소스 관련 에러
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "E006", "태그를 찾을 수 없습니다"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "E007", "게시글을 찾을 수 없습니다"),
    POST_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "E008", "게시글 상태를 찾을 수 없습니다"),

    // 서버 내부 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E999", "서버 내부 오류가 발생했습니다");

    private final HttpStatus status;  // HTTP 상태 코드
    private final String code;        // 에러 코드
    private final String message;     // 에러 메시지
}
