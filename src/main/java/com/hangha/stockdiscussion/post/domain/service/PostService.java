package com.hangha.stockdiscussion.post.domain.service;


import com.hangha.stockdiscussion.User.domain.entity.User;
import com.hangha.stockdiscussion.User.domain.repository.UserRepository;
import com.hangha.stockdiscussion.post.application.command.PostUpdateCommand;
import com.hangha.stockdiscussion.post.application.command.PostWriteCommand;
import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostService implements PostInterface {

    private final PostRepository postRepository;


    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;

    }

    @Override
    @Transactional
    public void writePost(PostWriteCommand command) {
        Post post = Post.builder()
                .userId(command.userId())
                .title("새로운 제목")
                .content("새로운 내용")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

         postRepository.save(post);
    }

    @Override
    @Transactional
    public void updatePost(PostUpdateCommand command) {
        Post post = postRepository.findById(command.postId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        post.updateTitleAndContent(command.title(), command.content());
        post.onUpdate();

        postRepository.save(post);
    }

}
