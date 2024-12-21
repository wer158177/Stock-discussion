package com.hangha.stockdiscussion.post.post_comments.domain.service;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.repository.PostRepository;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import com.hangha.stockdiscussion.post.post_comments.domain.repository.CommentsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService implements CommentInterface {

    private final CommentsRepository commentsRepository;
    private final PostRepository postRepository;

    public CommentService(CommentsRepository commentsRepository, PostRepository postRepository) {
        this.commentsRepository = commentsRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void writeComment(Long userId, CommentCommand command) {

        Post post = postRepository.findById(command.postId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 댓글 생성
        PostComments comment = PostComments.builder()
                .post(post)
                .userId(userId)// 게시글과 연결
                .comment(command.comment())  // 댓글 내용
                .createdAt(LocalDateTime.now())  // 생성 시간
                .updatedAt(LocalDateTime.now())  // 수정 시간
                .build();

        commentsRepository.save(comment);
    }

    @Override
    public void updateComment(Long userId, CommentUpdateCommand command) {
        PostComments comment = commentsRepository.findById(command.commentId())
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));


        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("자신이 작성한 댓글만 수정할 수 있습니다.");
        }

        // 댓글 내용 업데이트
        comment.updateComment(command.comment());  // 변경된 내용으로 업데이트
        comment.onUpdate();  // 수정 시간 갱신

        commentsRepository.save(comment);  // 댓글 저장
    }


    @Override
    public void deleteComment(Long postId, Long userId, Long commentId) {
        // 댓글 조회
        PostComments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        // 댓글 작성자인지 확인
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("댓글을 삭제할 권한이 없습니다.");
        }

        // 댓글 삭제
        commentsRepository.delete(comment);

    }
}
