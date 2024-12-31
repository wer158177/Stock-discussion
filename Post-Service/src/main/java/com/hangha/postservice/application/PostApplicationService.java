package com.hangha.postservice.application;

import com.hangha.common.event.model.UserActivityEvent;
import com.hangha.postservice.application.command.PostUpdateCommand;
import com.hangha.postservice.application.command.PostWriteCommand;
import com.hangha.postservice.controller.dto.PostRequestDto;
import com.hangha.postservice.controller.dto.PostStatusResponse;
import com.hangha.postservice.domain.service.PostInterface;
import com.hangha.postservice.domain.service.PostLikesService;
import com.hangha.postservice.domain.service.PostStatusService;
import com.hangha.postservice.event.UserActivityEventFactory;
import com.hangha.postservice.event.producer.UserActivityProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostApplicationService {
    private final PostInterface postinterface;
    private final PostStatusService postStatusService;
    private final PostLikesService postLikesService;
    private final UserActivityProducer producer;


    public PostApplicationService(PostInterface postinterface, PostStatusService postStatusService, PostLikesService postLikesService, UserActivityProducer producer) {
        this.postinterface = postinterface;
        this.postStatusService = postStatusService;

        this.postLikesService = postLikesService;
        this.producer = producer;
    }


    public void postWrite(Long userId, PostRequestDto postRequestDto){
        PostWriteCommand command = postRequestDto.WriteCommand(userId);
       Long postId = postinterface.writePost(command);
        UserActivityEvent event = UserActivityEventFactory.createPostCreateEvent(userId,postId, postRequestDto);
        producer.sendActivityEvent(event);
    }

    public  void postUpdate(Long userId, PostRequestDto postRequestDto){
        PostUpdateCommand command = postRequestDto.updateCommand(userId);
        postinterface.updatePost(command);
        UserActivityEvent event = UserActivityEventFactory.createPostUpdateEvent(userId, command.postId(),postRequestDto);
        producer.sendActivityEvent(event);
    }

    public  void postDelete(Long userId,Long postId){
        postinterface.deletePost(postId,userId);
        UserActivityEvent event = UserActivityEventFactory.createPostDeleteEvent(userId,postId);
        producer.sendActivityEvent(event);
    }

    public PostStatusResponse getPostStatus(Long postId) {
        return postStatusService.getPostStatusSummary(postId);
    }



    @Transactional
    public void likePost(Long postId, Long userId) {
        // 도메인 서비스 호출
        postLikesService.addLike(postId, userId);
        postStatusService.incrementLikes(postId);
        UserActivityEvent envet = UserActivityEventFactory.createLikeEvent(userId,postId);
        producer.sendActivityEvent(envet);

    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        // 도메인 서비스 호출
        postLikesService.removeLike(postId, userId);
        postStatusService.decrementLikes(postId);
        UserActivityEvent envet = UserActivityEventFactory.createUnlikeEvent(userId,postId);
        producer.sendActivityEvent(envet);
    }


}
