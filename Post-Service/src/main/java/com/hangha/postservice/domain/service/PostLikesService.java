package com.hangha.postservice.domain.service;

import com.hangha.postservice.domain.entity.Post;
import com.hangha.postservice.domain.entity.PostLikes;
import com.hangha.postservice.domain.repository.PostLikesRepository;
import com.hangha.postservice.domain.repository.PostRepository;
import com.hangha.postservice.exception.CustomException;
import com.hangha.postservice.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class PostLikesService {

    private final PostLikesRepository postLikesRepository;
    private final PostRepository postRepository;

    public PostLikesService(PostLikesRepository postLikesRepository, PostRepository postRepository) {
        this.postLikesRepository = postLikesRepository;
        this.postRepository = postRepository;
    }

    public void addLike(Long postId, Long userId) {
        Post post = findPostById(postId);

        // 좋아요 중복 체크
        if (postLikesRepository.existsByPostAndUserId(post, userId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "이미 좋아요를 눌렀습니다.");
        }

        // 좋아요 추가
        PostLikes postLike = new PostLikes(post, userId);
        postLikesRepository.save(postLike);
    }

    public void removeLike(Long postId, Long userId) {
        Post post = findPostById(postId);

        // 좋아요 삭제
        PostLikes postLike = postLikesRepository.findByPostAndUserId(post, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT, "좋아요를 누르지 않았습니다."));
        postLikesRepository.delete(postLike);
    }

    // 공통 게시글 조회 메서드
    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
}
