package com.hangha.postservice.domain.service;

import com.hangha.postservice.application.command.PostUpdateCommand;
import com.hangha.postservice.application.command.PostWriteCommand;
import com.hangha.postservice.controller.dto.PageResponseDto;
import com.hangha.postservice.controller.dto.PostResponseDto;
import com.hangha.postservice.domain.entity.PostStatus;
import com.hangha.postservice.domain.repository.PostRepository;
import com.hangha.postservice.domain.entity.Post;
import com.hangha.postservice.exception.CustomException;
import com.hangha.postservice.exception.ErrorCode;

import com.hangha.postservice.infrastructure.client.UserInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService implements PostInterface {

    private final PostRepository postRepository;
    private final UserInfoService userInfoService;

    public PostService(PostRepository postRepository, UserInfoService userInfoService) {
        this.postRepository = postRepository;
        this.userInfoService = userInfoService;
    }

    @Override
    public Long writePost(PostWriteCommand command) {
        // 게시글 생성
        Post post = Post.createPost(
                command.userId(),
                command.title(),
                command.content()
        );
        // DB에 저장
        postRepository.save(post);

        // 저장된 게시글의 ID를 반환
        return post.getId(); // 생성된 게시글의 ID 반환
    }

    @Override
    public void updatePost(PostUpdateCommand command) {
        // 게시글 조회
        Post post = findPostByIdAndValidateUser(command.postId(), command.userId());

        // 게시글 수정
        post.updateTitleAndContent(command.title(), command.content());

        // JPA 변경 감지를 위해 명시적으로 저장
        postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        // 게시글 조회
        Post post = findPostByIdAndValidateUser(postId, userId);

        // 게시글 삭제
        postRepository.delete(post);
    }

    // 공통 게시글 조회 및 작성자 검증 메서드
    private Post findPostByIdAndValidateUser(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 게시글을 찾을 수 없는 경우
        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "자신이 작성한 게시글만 수정 또는 삭제할 수 있습니다."); // 작성자 검증 실패
        }
        return post;
    }




    public boolean existsById(Long postId) {
        return postRepository.existsById(postId);
    }





    @Override
    @Transactional(readOnly = true)
    public Page<Post> findAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }



}








