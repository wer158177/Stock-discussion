package com.hangha.domain.service;


import com.hangha.application.command.CommentCommand;
import com.hangha.application.command.CommentUpdateCommand;

import com.hangha.domain.entity.PostComments;
import com.hangha.domain.repository.CommentsRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;



import java.util.List;

@Service
public class CommentService {

    private final CommentsRepository commentsRepository;
    private final RestTemplate restTemplate;
    private final String postServiceUrl = "http://localhost:8083/api/posts";


    public CommentService(CommentsRepository commentsRepository, RestTemplate restTemplate) {
        this.commentsRepository = commentsRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void writeComment(Long userId, CommentCommand command) {
        validatePostExists(command.postId());
        validateParentComment(command.parentId());

        PostComments comment = PostComments.createComment(command.postId(), userId, command.content(), command.parentId());
        updatePostCommentCount(command.postId(),true);
        commentsRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long userId, CommentUpdateCommand command) {
        PostComments comment = findCommentById(command.commentId());

        validateUserAuthorization(comment.getUserId(), userId);

        comment.updateComment(command.content());
        comment.onUpdate();

        commentsRepository.save(comment);

    }

    @Transactional
    public void deleteComment(Long postId, Long userId, Long commentId) {
        PostComments comment = findCommentById(commentId);
        validateUserAuthorization(comment.getUserId(), userId);
        if (comment.getParentId() == null) {
            updatePostCommentCount(postId,true);
            commentsRepository.deleteRepliesByParentId(comment.getId());
        }

        commentsRepository.delete(comment);
    }

    public List<PostComments> findParentCommentsByPostId(Long postId) {
        return commentsRepository.findByPostIdAndParentIdIsNull(postId);
    }

    public List<PostComments> findRepliesByParentId(Long parentId) {
        return commentsRepository.findRepliesByParentId(parentId);
    }

    public void validatePostExists(Long postId) {
        String url = "http://localhost:8083/posts/" + postId + "/exists";
        Boolean exists = restTemplate.getForObject(url, Boolean.class);
        if (Boolean.FALSE.equals(exists)) {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
    }

    private void updatePostCommentCount(Long postId, boolean isIncrement) {
        String url = postServiceUrl + "/" + postId + "/comments/countUp";
        CommentCountRequest countRequest = new CommentCountRequest(postId, isIncrement);

        try {
            restTemplate.postForEntity(url, countRequest, Void.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Client error while updating comment count: " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Server error while updating comment count: " + e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new RuntimeException("Unexpected error while updating comment count for postId: " + postId, e);
        }
    }


    // Helper method: 댓글 조회
    private PostComments findCommentById(Long commentId) {
        return commentsRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
    }

    // Helper method: 부모 댓글 검증
    private void validateParentComment(Long parentId) {
        if (parentId != null) {
            PostComments parentComment = findCommentById(parentId);
            if (parentComment.getParentId() != null) {
                throw new RuntimeException("대대댓글은 허용되지 않습니다.");
            }
        }
    }

    // Helper method: 사용자 권한 검증
    private void validateUserAuthorization(Long commentUserId, Long userId) {
        if (!commentUserId.equals(userId)) {
            throw new RuntimeException("자신이 작성한 댓글만 수정/삭제할 수 있습니다.");
        }
    }



}
