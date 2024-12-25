package com.hangha.application;

import com.hangha.application.command.PostUpdateCommand;
import com.hangha.application.command.PostWriteCommand;
import com.hangha.controller.dto.CommentCountRequest;
import com.hangha.controller.dto.PostRequestDto;
import com.hangha.controller.dto.PostStatusResponse;
import com.hangha.domain.service.PostLikesService;
import com.hangha.domain.service.PostService;
import com.hangha.domain.service.PostStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostApplicationService {
    private final PostService postService;
    private final PostStatusService postStatusService;
    private final PostLikesService postLikesService;


    public PostApplicationService( PostService postService, PostStatusService postStatusService, PostLikesService postLikesService) {
        this.postService = postService;
        this.postStatusService = postStatusService;
        this.postLikesService = postLikesService;
    }


    public void postWrite(Long userId, PostRequestDto postRequestDto){
        PostWriteCommand command = postRequestDto.WriteCommand(userId);
        postService.writePost(command);
    }

    public  void postUpdate(Long userId, PostRequestDto postRequestDto){
        PostUpdateCommand command = postRequestDto.updateCommand(userId);
        postService.updatePost(command);
    }

    public  void postDelete(Long userId,Long postId){
        postService.deletePost(postId,userId);
    }

    public PostStatusResponse getPostStatus(Long postId) {
        return postStatusService.getPostStatusSummary(postId);
    }

    //댓글수 증가/감소
    public void  updateCommentsCount(CommentCountRequest commentCountRequest){
        postStatusService.updateCommentCount(commentCountRequest);
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
