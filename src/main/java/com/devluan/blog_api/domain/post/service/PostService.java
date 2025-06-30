package com.devluan.blog_api.domain.post.service;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.domain.post.mapper.PostMapper;
import com.devluan.blog_api.domain.post.model.Post;
import com.devluan.blog_api.domain.post.repository.PostRepository;
import com.devluan.blog_api.domain.exception.DomainException;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.infrastructure.logger.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final LoggerService logger;
    private final UserRepository userRepository;

    public PostRegisterResponse createPost(PostRegisterRequest request){
        logger.info("Attempting to create a new post.");
        if (request == null){
            logger.warn("Post creation request is null.");
            throw new IllegalArgumentException("Post cannot be null");
        }

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for post creation.", request.authorId().toString());
                    return new DomainException("User not found", "USER_NOT_FOUND");
                });

        Post newPost = postMapper.toEntity(request);
        newPost.setAuthor(author);
        postRepository.save(newPost);
        logger.info("Post created successfully with ID: {}", newPost.getPostId().toString());
        return postMapper.toResponse(newPost);
    }

    public Optional<PostRegisterResponse> getPostById(UUID postId) {
        logger.info("Attempting to retrieve post with ID: {}", postId.toString());
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            logger.debug("Post with ID: {} found.", postId.toString());
        } else {
            logger.warn("Post with ID: {} not found.", postId.toString());
        }
        return post.map(postMapper::toResponse);
    }

    public PostRegisterResponse updatePost(UUID postId, PostRegisterRequest request) {
        logger.info("Attempting to update post with ID: {}", postId.toString());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post with ID: {} not found for update.", postId.toString());
                    return new DomainException("Post not found", "POST_NOT_FOUND");
                });

        post.updateTitle(request.title());
        post.updateContent(request.content());

        Post updatedPost = postRepository.save(post);
        logger.info("Post with ID: {} updated successfully.", postId.toString());
        return postMapper.toResponse(updatedPost);
    }

    public void deletePost(UUID postId) {
        logger.info("Attempting to delete post with ID: {}", postId.toString());
        if (!postRepository.existsById(postId)) {
            logger.warn("Post with ID: {} not found for deletion.", postId.toString());
            throw new DomainException("Post not found", "POST_NOT_FOUND");
        }
        postRepository.deleteById(postId);
        logger.info("Post with ID: {} deleted successfully.", postId.toString());
    }
}
