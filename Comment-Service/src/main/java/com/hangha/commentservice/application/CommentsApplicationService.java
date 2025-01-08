package com.hangha.commentservice.application;



import com.hangha.commentservice.application.command.CommentCommand;
import com.hangha.commentservice.application.command.CommentEventResult;
import com.hangha.commentservice.application.command.CommentUpdateCommand;
import com.hangha.commentservice.controller.dto.CommentsRequestDto;
import com.hangha.commentservice.controller.dto.SimpleCommentResponseDto;
import com.hangha.commentservice.domain.entity.PostComments;
import com.hangha.commentservice.domain.service.CommentLikesService;
import com.hangha.commentservice.domain.service.CommentQueryService;
import com.hangha.commentservice.domain.service.CommentService;
import com.hangha.commentservice.event.UserActivityEventFactory;
import com.hangha.commentservice.event.UserActivityProducer;
import com.hangha.commentservice.feignclient.UserFeignClient;
import com.hangha.common.dto.UserResponseDto;
import com.hangha.common.event.model.UserActivityEvent;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentsApplicationService {
    private final CommentService commentService;
    private final UserActivityProducer userActivityProducer;
    private final CommentQueryService commentQueryService;


    private final CommentLikesService commentLikesService;
    public CommentsApplicationService(CommentService commentService, UserActivityProducer userActivityProducer, CommentQueryService commentQueryService, CommentLikesService commentLikesService) {
        this.commentService = commentService;
        this.userActivityProducer = userActivityProducer;
        this.commentQueryService = commentQueryService;

        this.commentLikesService = commentLikesService;
    }


    public void commentWrite(Long userId, CommentsRequestDto commentsRequestDto){
        CommentCommand command = commentsRequestDto.writeCommand(userId);
        CommentEventResult eventResult = commentService.writeComment(userId,command);

        UserActivityEvent event = eventResult.isReply()
                ? UserActivityEventFactory.createReplyCreateEvent(userId, eventResult, command)
                : UserActivityEventFactory.createCommentCreateEvent(userId, eventResult, command);

        userActivityProducer.sendActivityEvent(event);


    }

    public void commentUpdate(Long userId, CommentsRequestDto commentsRequestDto){
        CommentUpdateCommand command = commentsRequestDto.updateCommand(userId);
        CommentEventResult eventResult = commentService.updateComment(userId,command);

        UserActivityEvent event = eventResult.isReply()
                ? UserActivityEventFactory.createReplyUpdateEvent(userId, eventResult, command)
                : UserActivityEventFactory.createCommentUpdateEvent(userId, eventResult, command);


        userActivityProducer.sendActivityEvent(event);
    }


    public void commentDelete(Long userId,Long commentId,Long postId){
        CommentEventResult eventResult =commentService.deleteComment(userId,commentId,postId);

        UserActivityEvent event = eventResult.isReply()
                ? UserActivityEventFactory.createReplyDeleteEvent(userId, eventResult, postId)
                : UserActivityEventFactory.createCommentDeleteEvent(userId, eventResult, postId);

        userActivityProducer.sendActivityEvent(event);


    }




    @Transactional
    public void likeComment(Long commentId, Long userId,Long postId) {
       CommentEventResult eventResult = commentLikesService.likeComment(commentId, userId);

        UserActivityEvent event = eventResult.isReply()
                ? UserActivityEventFactory.createReplyLikeEvent(userId, eventResult,postId)
                : UserActivityEventFactory.createCommentLikeEvent(userId, eventResult,postId);

        userActivityProducer.sendActivityEvent(event);

    }

    @Transactional
    public void unlikeComment(Long commentId, Long userId,Long postId) {
        CommentEventResult eventResult = commentLikesService.unlikeComment(commentId, userId);

        UserActivityEvent event = eventResult.isReply()
                ? UserActivityEventFactory.createReplyUnlikeEvent(userId, eventResult,postId)
                : UserActivityEventFactory.createCommentUnlikeEvent(userId, eventResult,postId);

        userActivityProducer.sendActivityEvent(event);
    }



    @Transactional
    public List<SimpleCommentResponseDto> getParentComments(Long postId, Long userId) {
        return commentQueryService.getParentComments(postId, userId);
    }

    @Transactional
    public List<SimpleCommentResponseDto> getReplies(Long parentId, Long userId) {
        return commentQueryService.getReplies(parentId, userId);
    }




}


