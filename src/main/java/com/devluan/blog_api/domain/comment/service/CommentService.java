package com.devluan.blog_api.domain.comment.service;

import com.devluan.blog_api.application.dto.comment.request.CommentRegisterRequest;
import com.devluan.blog_api.application.dto.comment.response.CommentRegisterResponse;
import com.devluan.blog_api.domain.comment.model.Comment;
import com.devluan.blog_api.domain.comment.repository.CommentRepository;
import com.devluan.blog_api.domain.exception.DomainException;
import com.devluan.blog_api.domain.post.model.Post;
import com.devluan.blog_api.domain.post.repository.PostRepository;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.infrastructure.logger.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LoggerService logger;

    public CommentRegisterResponse createComment(CommentRegisterRequest request) {
        logger.info("Attempting to create a new comment.");
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for comment creation.", request.authorId());
                    return new DomainException("User not found", "USER_NOT_FOUND");
                });
        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> {
                    logger.warn("Post with ID: {} not found for comment creation.", request.postId());
                    return new DomainException("Post not found", "POST_NOT_FOUND");
                });

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setAuthor(author);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created successfully with ID: {}", savedComment.getCommentId());
        return toResponse(savedComment);
    }

    public CommentRegisterResponse updateComment(UUID commentId, CommentRegisterRequest request) {
        logger.info("Attempting to update comment with ID: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("Comment with ID: {} not found for update.", commentId);
                    return new DomainException("Comment not found", "COMMENT_NOT_FOUND");
                });

        comment.setContent(request.content());

        Comment updatedComment = commentRepository.save(comment);
        logger.info("Comment with ID: {} updated successfully.", commentId);
        return toResponse(updatedComment);
    }

    public void deleteComment(UUID commentId) {
        logger.info("Attempting to delete comment with ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            logger.warn("Comment with ID: {} not found for deletion.", commentId);
            throw new DomainException("Comment not found", "COMMENT_NOT_FOUND");
        }
        commentRepository.deleteById(commentId);
        logger.info("Comment with ID: {} deleted successfully.", commentId);
    }

    private CommentRegisterResponse toResponse(Comment comment) {
        return new CommentRegisterResponse(
                comment.getCommentId(),
                comment.getContent(),
                comment.getAuthor().getUserId(),
                comment.getPost().getPostId(),
                comment.getCreatedAt()
        );
    }
}
