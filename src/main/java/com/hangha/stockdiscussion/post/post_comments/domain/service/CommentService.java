package com.hangha.stockdiscussion.post.post_comments.domain.service;

import com.hangha.stockdiscussion.post.domain.entity.Post;
import com.hangha.stockdiscussion.post.domain.repository.PostRepository;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentCommand;
import com.hangha.stockdiscussion.post.post_comments.application.command.CommentUpdateCommand;
import com.hangha.stockdiscussion.post.post_comments.controller.dto.SimpleCommentResponseDto;
import com.hangha.stockdiscussion.post.post_comments.domain.entity.PostComments;
import com.hangha.stockdiscussion.post.post_comments.domain.repository.CommentsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentsRepository commentsRepository;
    private final PostRepository postRepository;

    public CommentService(CommentsRepository commentsRepository, PostRepository postRepository) {
        this.commentsRepository = commentsRepository;
        this.postRepository = postRepository;
    }


    public void writeComment(Long userId, CommentCommand command) {

        Post post = postRepository.findById(command.postId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 댓글 생성
        PostComments comment = PostComments.builder()
                .post(post)
                .userId(userId)// 게시글과 연결
                .content(command.content())  // 댓글 내용
                .parentId(command.parentId()) //부모댓글
                .createdAt(LocalDateTime.now())  // 생성 시간
                .updatedAt(LocalDateTime.now())  // 수정 시간
                .build();

        commentsRepository.save(comment);
    }


    public void updateComment(Long userId, CommentUpdateCommand command) {
        PostComments comment = commentsRepository.findById(command.commentId())
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));


        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("자신이 작성한 댓글만 수정할 수 있습니다.");
        }

        // 댓글 내용 업데이트
        comment.updateComment(command.content());  // 변경된 내용으로 업데이트
        comment.onUpdate();  // 수정 시간 갱신

        commentsRepository.save(comment);  // 댓글 저장
    }



    public void deleteComment(Long postId, Long userId, Long commentId) {
        PostComments comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }

        // 대댓글이 있는 경우 먼저 삭제
        if (comment.getParentId() == null) {
            commentsRepository.deleteRepliesByParentId(comment.getId());
        }

        // 부모 댓글 또는 대댓글 삭제
        commentsRepository.delete(comment);
    }




    public List<SimpleCommentResponseDto> findParentCommentsByPostId(Long postId) {
        // 부모 댓글 조회 후 DTO로 변환
        return commentsRepository.findByPostIdAndParentIdIsNull(postId).stream()
                .map(comment -> new SimpleCommentResponseDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUserId(),
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<SimpleCommentResponseDto> findRepliesByParentId(Long parentId) {
        // 대댓글 조회 후 DTO로 변환
        return commentsRepository.findRepliesByParentId(parentId).stream()
                .map(reply -> new SimpleCommentResponseDto(
                        reply.getId(),
                        reply.getContent(),
                        reply.getUserId(),
                        reply.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }




}
