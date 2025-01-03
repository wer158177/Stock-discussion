package com.hangha.activityservice.domain.Service;

public class AlarmMessage {

    // 게시글 관련 알람
    public static final String POST_CREATED = "%s님이 게시글을 작성했습니다.";
    public static final String POST_COMMENTED = "%s님이 내 게시글에 댓글을 달았습니다.";
    public static final String POST_LIKED = "%s님이 내 게시글에 좋아요를 눌렀습니다.";

    // 댓글 관련 알람
    public static final String COMMENT_CREATED = "%s님이 댓글을 작성했습니다.";
    public static final String COMMENT_COMMENTED = "%s님이 내 댓글에 댓글을 달았습니다.";
    public static final String COMMENT_LIKED = "%s님이 내 댓글에 좋아요를 눌렀습니다.";

    // 팔로우 관련 알람
    public static final String FOLLOWED = "%s님이 %s님을 팔로우했습니다.";
    public static final String UNFOLLOWED = "%s님이 %s님을 언팔로우했습니다.";

    // 팔로우한 사용자의 활동 관련 알람
    public static final String FOLLOWING_POST_CREATED = "%s님이 게시글을 작성했습니다.";
    public static final String FOLLOWING_COMMENT_CREATED = "%s님이 댓글을 작성했습니다.";
}

