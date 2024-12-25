package com.hangha.domain.service;

import com.hangha.application.command.PostUpdateCommand;
import com.hangha.domain.repository.PostRepository;
import com.hangha.application.command.PostWriteCommand;
import com.hangha.domain.entity.Post;
import org.springframework.stereotype.Service;

@Service
public class PostService  {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public void writePost(PostWriteCommand command) {
        // 게시글 생성
        Post post = Post.createPost(
                command.userId(),
                command.title(),
                command.content()
        );
        postRepository.save(post);
    }


    public void updatePost(PostUpdateCommand command) {
        // 게시글 조회
        Post post = findPostByIdAndValidateUser(command.postId(), command.userId());

        // 게시글 수정
        post.updateTitleAndContent(command.title(), command.content());

        // JPA 변경 감지를 위해 명시적으로 저장
        postRepository.save(post);
    }


    public void deletePost(Long postId, Long userId) {
        // 게시글 조회
        Post post = findPostByIdAndValidateUser(postId, userId);

        // 게시글 삭제
        postRepository.delete(post);
    }

    // 공통 게시글 조회 및 작성자 검증 메서드
    private Post findPostByIdAndValidateUser(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("자신이 작성한 게시글만 수정할 수 있습니다.");
        }
        return post;
    }


    public boolean existsById(Long postId) {
        return postRepository.existsById(postId);
    }

}
