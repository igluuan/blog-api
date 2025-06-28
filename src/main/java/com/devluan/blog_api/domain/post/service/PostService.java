package com.devluan.blog_api.domain.post.service;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.domain.post.mapper.PostMapper;
import com.devluan.blog_api.domain.post.model.Post;
import com.devluan.blog_api.domain.post.repository.PostRepository;
import com.devluan.blog_api.domain.exception.DomainException;
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

    public PostRegisterResponse createPost(PostRegisterRequest request){
        logger.info("Attempting to create a new post.");
        if (request == null){
            logger.warn("Post creation request is null.");
            throw new IllegalArgumentException("Post cannot be null");
        }
        Post newPost = postMapper.toEntity(request);
        postRepository.save(newPost);
        logger.info("Post created successfully with ID: {}", newPost.getPostId());
        return postMapper.toResponse(newPost);
    }

    public Optional<PostRegisterResponse> getPostById(UUID postId) {
        logger.info("Attempting to retrieve post with ID: {}", postId);
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            logger.debug("Post with ID: {} found.", postId);
        } else {
            logger.warn("Post with ID: {} not found.", postId);
        }
        return post.map(postMapper::toResponse);
    }

    public PostRegisterResponse updatePost(UUID postId, PostRegisterRequest request) {
        logger.info("Attempting to update post with ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post with ID: {} not found for update.", postId);
                    return new DomainException("Post not found", "POST_NOT_FOUND");
                });

        post.updateTitle(request.title());
        post.updateContent(request.content());

        Post updatedPost = postRepository.save(post);
        logger.info("Post with ID: {} updated successfully.", postId);
        return postMapper.toResponse(updatedPost);
    }

    public void deletePost(UUID postId) {
        logger.info("Attempting to delete post with ID: {}", postId);
        if (!postRepository.existsById(postId)) {
            logger.warn("Post with ID: {} not found for deletion.", postId);
            throw new DomainException("Post not found", "POST_NOT_FOUND");
        }
        postRepository.deleteById(postId);
        logger.info("Post with ID: {} deleted successfully.", postId);
    }
}
