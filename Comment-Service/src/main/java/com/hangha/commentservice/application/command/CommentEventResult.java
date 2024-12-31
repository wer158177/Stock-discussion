package com.hangha.commentservice.application.command;

/**
 * 댓글 또는 대댓글 작성 결과를 나타내는 클래스.
 */
public record CommentEventResult(
        Long commentId,  // 작성된 댓글/대댓글 ID
        Long parentId,   // 부모 댓글 ID (대댓글인 경우에만 존재)
        boolean isReply  // 대댓글 여부를 나타내는 플래그
) {
    // 대댓글 여부를 쉽게 확인할 수 있는 메서드
    public boolean isReply() {
        return parentId != null;
    }
}
