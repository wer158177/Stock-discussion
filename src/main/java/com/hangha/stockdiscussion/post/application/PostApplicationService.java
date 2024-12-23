package com.hangha.stockdiscussion.post.application;

import com.hangha.stockdiscussion.post.application.command.PostUpdateCommand;
import com.hangha.stockdiscussion.post.application.command.PostWriteCommand;
import com.hangha.stockdiscussion.post.controller.dto.PostRequestDto;
import com.hangha.stockdiscussion.post.controller.dto.PostStatusResponse;
import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.service.PostInterface;
import com.hangha.stockdiscussion.post.domain.service.PostLikesService;
import com.hangha.stockdiscussion.post.domain.service.PostService;
import com.hangha.stockdiscussion.post.domain.service.PostStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostApplicationService {
    private final PostInterface postinterface;
    private final PostStatusService postStatusService;
    private final PostLikesService postLikesService;


    public PostApplicationService(PostInterface postinterface, PostStatusService postStatusService, PostLikesService postLikesService) {
        this.postinterface = postinterface;
        this.postStatusService = postStatusService;

        this.postLikesService = postLikesService;
    }


    public void postWrite(Long userId, PostRequestDto postRequestDto){
        PostWriteCommand command = postRequestDto.WriteCommand(userId);
        postinterface.writePost(command);
    }

    public  void postUpdate(Long userId, PostRequestDto postRequestDto){
        PostUpdateCommand command = postRequestDto.updateCommand(userId);
        postinterface.updatePost(command);
    }

    public  void postDelete(Long userId,Long postId){
        postinterface.deletePost(postId,userId);
    }

    public PostStatusResponse getPostStatus(Long postId) {
        return postStatusService.getPostStatusSummary(postId);
    }



    @Transactional
    public void likePost(Long postId, Long userId) {
        // 도메인 서비스 호출
        postLikesService.addLike(postId, userId);
        postStatusService.incrementLikes(postId);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        // 도메인 서비스 호출
        postLikesService.removeLike(postId, userId);
        postStatusService.decrementLikes(postId);
    }


}
