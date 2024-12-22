package com.hangha.stockdiscussion.post.domain.service;


import com.hangha.stockdiscussion.post.application.command.PostUpdateCommand;
import com.hangha.stockdiscussion.post.application.command.PostWriteCommand;
import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService implements PostInterface {

    private final PostRepository postRepository;


    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;

    }

    @Override
    public void writePost(PostWriteCommand command) {
        Post post = Post.builder()
                .userId(command.userId())
                .title(command.title()) // 수정된 부분
                .content(command.content()) // 수정된 부분
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }

    @Override
    public void updatePost(PostUpdateCommand command) {
        // 게시글 조회
        Post post = postRepository.findById(command.postId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자 검증
        if (!post.getUserId().equals(command.userId())) {
            throw new RuntimeException("자신이 작성한 게시글만 수정할 수 있습니다.");
        }

        // 게시글 수정
        post.updateTitleAndContent(command.title(), command.content());

        // 변경 사항 저장
        postRepository.save(post); // JPA 변경 감지를 보장하기 위해 명시적으로 호출
    }

    @Override
    public void deletePost(Long postId,Long userId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("자신이 작성한 게시글만 삭제할 수 있습니다.");
        }
        postRepository.delete(post);
    }

}
