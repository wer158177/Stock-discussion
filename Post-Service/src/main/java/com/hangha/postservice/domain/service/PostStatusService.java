package com.hangha.postservice.domain.service;

import com.hangha.postservice.domain.repository.PostStatusRepository;
import com.hangha.postservice.controller.dto.PostStatusResponse;
import com.hangha.postservice.domain.entity.Post;
import com.hangha.postservice.domain.entity.PostStatus;
import com.hangha.postservice.domain.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class PostStatusService {

    private final PostRepository postRepository;
    private final PostStatusRepository postStatusRepository;

    public PostStatusService(PostRepository postRepository, PostStatusRepository postStatusRepository) {
        this.postRepository = postRepository;
        this.postStatusRepository = postStatusRepository;
    }


    public void incrementLikes(Long postId) {
        PostStatus postStatus = getPostStatus(postId);
        postStatus.incrementLikesCount();
        postStatusRepository.save(postStatus);
    }

    public void decrementLikes(Long postId) {
        PostStatus postStatus = getPostStatus(postId);
        postStatus.decrementLikesCount();
        postStatusRepository.save(postStatus);
    }

    // 댓글 수 업데이트
    public void updateCommentCount(Long postId, boolean isIncrement) {
        PostStatus postStatus = getPostStatus(postId);
        if (isIncrement) {
            postStatus.incrementCommentsCount();
        } else {
            postStatus.decrementCommentsCount();
        }
        postStatusRepository.save(postStatus);
    }

    // 조회수 증가
    public void increaseViewCount(Long postId) {
        PostStatus postStatus = getPostStatus(postId);
        postStatus.incrementViewsCount();
        postStatusRepository.save(postStatus);
    }


    public PostStatusResponse getPostStatusSummary(Long postId) {
        PostStatus postStatus = getPostStatus(postId);
        return new PostStatusResponse(
                postStatus.getLikesCount(),
                postStatus.getCommentsCount(),
                postStatus.getViewsCount()
        );
    }

    // 상태 가져오기
    private PostStatus getPostStatus(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        return postStatusRepository.findByPost(post)
                .orElseThrow(() -> new IllegalArgumentException("게시글 상태가 존재하지 않습니다."));
    }


}

