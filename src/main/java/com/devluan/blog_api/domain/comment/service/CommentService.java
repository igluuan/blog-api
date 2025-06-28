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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentRegisterResponse createComment(CommentRegisterRequest request) {
        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new DomainException("User not found", "USER_NOT_FOUND"));
        Post post = postRepository.findById(request.postId())
                .orElseThrow(() -> new DomainException("Post not found", "POST_NOT_FOUND"));

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setAuthor(author);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        return toResponse(savedComment);
    }

    public CommentRegisterResponse updateComment(UUID commentId, CommentRegisterRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DomainException("Comment not found", "COMMENT_NOT_FOUND"));

        comment.setContent(request.content());

        Comment updatedComment = commentRepository.save(comment);
        return toResponse(updatedComment);
    }

    public void deleteComment(UUID commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new DomainException("Comment not found", "COMMENT_NOT_FOUND");
        }
        commentRepository.deleteById(commentId);
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
