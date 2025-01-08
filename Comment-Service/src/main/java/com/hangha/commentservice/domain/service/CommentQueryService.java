package com.hangha.commentservice.domain.service;

import com.hangha.commentservice.controller.dto.SimpleCommentResponseDto;
import com.hangha.commentservice.domain.entity.PostComments;
import com.hangha.commentservice.feignclient.UserFeignClient;
import com.hangha.common.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentQueryService {
    private final CommentService commentService;
    private final UserFeignClient userFeignClient;
    private final CommentLikesService commentLikesService;

    public List<SimpleCommentResponseDto> getParentComments(Long postId, Long userId) {
        List<PostComments> parentComments = commentService.findParentCommentsByPostId(postId);
        List<Long> commentIds = parentComments.stream()
                .map(PostComments::getId)
                .collect(Collectors.toList());

        Set<Long> likedCommentIds = commentLikesService.getLikedCommentIds(userId, commentIds);

        return parentComments.stream()
                .map(comment -> {
                    UserResponseDto userInfo = userFeignClient.getUserInfo(comment.getUserId());
                    return new SimpleCommentResponseDto(
                            comment,
                            userInfo,
                            likedCommentIds.contains(comment.getId()),
                            null
                    );
                })
                .collect(Collectors.toList());
    }

    public List<SimpleCommentResponseDto> getReplies(Long parentId, Long userId) {
        List<PostComments> replies = commentService.findRepliesByParentId(parentId);
        List<Long> replyIds = replies.stream()
                .map(PostComments::getId)
                .collect(Collectors.toList());

        Set<Long> likedReplyIds = commentLikesService.getLikedCommentIds(userId, replyIds);

        return replies.stream()
                .map(reply -> {
                    UserResponseDto userInfo = userFeignClient.getUserInfo(reply.getUserId());
                    return new SimpleCommentResponseDto(
                            reply,
                            userInfo,
                            likedReplyIds.contains(reply.getId()),
                            Collections.emptyList()
                    );
                })
                .collect(Collectors.toList());
    }
}
