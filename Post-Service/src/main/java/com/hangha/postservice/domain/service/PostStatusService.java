package com.hangha.postservice.domain.service;

import com.hangha.postservice.controller.dto.PostStatusResponse;
import com.hangha.postservice.domain.entity.Post;
import com.hangha.postservice.domain.entity.PostStatus;
import com.hangha.postservice.domain.repository.PostRepository;
import com.hangha.postservice.domain.repository.PostStatusRepository;
import com.hangha.postservice.exception.CustomException;
import com.hangha.postservice.exception.ErrorCode;
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
        if (postStatus.getLikesCount() <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "좋아요 수는 0보다 작을 수 없습니다.");
        }
        postStatus.decrementLikesCount();
        postStatusRepository.save(postStatus);
    }

    // 댓글 수 업데이트
    public void updateCommentCount(Long postId, boolean isIncrement) {
        PostStatus postStatus = getPostStatus(postId);
        if (isIncrement) {
            postStatus.incrementCommentsCount();
        } else {
            if (postStatus.getCommentsCount() <= 0) {
                throw new CustomException(ErrorCode.INVALID_INPUT, "댓글 수는 0보다 작을 수 없습니다.");
            }
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
        validatePostId(postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return postStatusRepository.findByPost(post)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_STATUS_NOT_FOUND));
    }

    // postId 유효성 검증
    private void validatePostId(Long postId) {
        if (postId == null || postId <= 0) {
            throw new CustomException(ErrorCode.INVALID_POST_ID);
        }
    }
}
