package com.hangha.commentservice.domain.service;



import com.hangha.commentservice.application.command.CommentCommand;
import com.hangha.commentservice.application.command.CommentUpdateCommand;
import com.hangha.commentservice.feignclient.PostFeignClient;
import com.hangha.commentservice.domain.entity.PostComments;
import com.hangha.commentservice.domain.repository.CommentsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;




import java.util.List;

@Service
public class CommentService {

    private final CommentsRepository commentsRepository;
    private final PostFeignClient postFeignClient;


    public CommentService(CommentsRepository commentsRepository, PostFeignClient postFeignClient) {
        this.commentsRepository = commentsRepository;
        this.postFeignClient = postFeignClient;

    }

    @Transactional
    public void writeComment(Long userId, CommentCommand command) {
        isPostAvailable(command.postId());
        validateParentComment(command.parentId());
        PostComments comment = PostComments.createComment(command.postId(), userId, command.content(), command.parentId());
        postFeignClient.updateCommentCount(command.postId(),true);
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
            postFeignClient.updateCommentCount(comment.getPostId(), false);
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

    public boolean isPostAvailable(Long postId) {
        return postFeignClient.doesPostExist(postId);
    }


}