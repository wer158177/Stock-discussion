package com.hangha.postservice.application;

import com.hangha.common.dto.UserResponseDto;
import com.hangha.common.event.model.UserActivityEvent;
import com.hangha.postservice.application.command.PostUpdateCommand;
import com.hangha.postservice.application.command.PostWriteCommand;
import com.hangha.postservice.controller.dto.PageResponseDto;
import com.hangha.postservice.controller.dto.PostRequestDto;
import com.hangha.postservice.controller.dto.PostResponseDto;
import com.hangha.postservice.controller.dto.PostStatusResponse;
import com.hangha.postservice.domain.entity.Post;
import com.hangha.postservice.domain.service.*;

import com.hangha.postservice.infrastructure.client.UserInfoService;
import com.hangha.postservice.infrastructure.event.UserActivityEventFactory;
import com.hangha.postservice.infrastructure.event.producer.UserActivityProducer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostApplicationService {
    private final PostInterface postinterface;
    private final PostStatusService postStatusService;
    private final PostLikesService postLikesService;
    private final TagService tagService;
    private final UserActivityProducer producer;
    private final UserInfoService userInfoService;
    private final PostService postService;


    public PostApplicationService(PostInterface postinterface, PostStatusService postStatusService, PostLikesService postLikesService, TagService tagService, UserActivityProducer producer, UserInfoService userInfoService, PostService postService) {
        this.postinterface = postinterface;
        this.postStatusService = postStatusService;
        this.postLikesService = postLikesService;
        this.tagService = tagService;
        this.producer = producer;
        this.userInfoService = userInfoService;
        this.postService = postService;
    }


    @Transactional
    public void postWrite(Long userId, PostRequestDto postRequestDto){
        PostWriteCommand command = postRequestDto.WriteCommand(userId);
        Long postId = postinterface.writePost(command);
        tagService.saveTags(command.tags(), postId);
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

    public PageResponseDto<PostResponseDto> getPosts(Pageable pageable) {
        Page<Post> posts = postService.findAllPosts(pageable);

        List<PostResponseDto> dtos = posts.getContent().stream()
                .map(post -> {
                    UserResponseDto userInfo = userInfoService.getUserInfo(post.getUserId());
                    List<String> tags = tagService.getTagsForPost(post.getId());

                    return new PostResponseDto(
                            post.getId().toString(),
                            userInfo.getUsername(),
                            userInfo.getImageUrl(),
                            post.getContent(),
                            null,
                            post.getPostStatus().getLikesCount(),
                            post.getCreatedAt(),
                            tags
                    );
                })
                .collect(Collectors.toList());

        return new PageResponseDto<>(new PageImpl<>(dtos, pageable, posts.getTotalElements()));
    }


}
