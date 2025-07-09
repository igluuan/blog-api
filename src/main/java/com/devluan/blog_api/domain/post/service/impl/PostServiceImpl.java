package com.devluan.blog_api.domain.post.service.impl;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.application.service.post.PostApplicationService;
import com.devluan.blog_api.application.dto.post.response.PostResponseDTO;
import com.devluan.blog_api.domain.exception.*;
import com.devluan.blog_api.domain.post.mapper.PostMapper;
import com.devluan.blog_api.domain.post.model.Post;
import com.devluan.blog_api.domain.post.repository.PostRepository;
import com.devluan.blog_api.domain.comment.repository.CommentRepository;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.infrastructure.logger.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostApplicationService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final LoggerService logger;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostRegisterResponse createPost(PostRegisterRequest request){
        logger.info("Attempting to create a new post.");
        if (request == null){
            logger.warn("Post creation request is null.");
            throw new InvalidPostDataException("Post cannot be null", "INVALID_POST_DATA");
        }

        User author = userRepository.findById(request.authorId())
                .orElseThrow(() -> {
                    logger.warn("User with ID: {} not found for post creation.", request.authorId().toString());
                    return new UserNotFoundException("User not found", "USER_NOT_FOUND");
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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post with ID: {} not found for update.", postId.toString());
                    return new PostNotFoundException("Post not found", "POST_NOT_FOUND");
                });

        post.updateTitle(request.title());
        post.updateContent(request.content());

        Post updatedPost = postRepository.save(post);
        logger.info("Post with ID: {} updated successfully.", postId.toString());
        return postMapper.toResponse(updatedPost);
    }

    public void deletePost(UUID postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Post with ID: {} not found for deletion.", postId.toString());
                    return new PostNotFoundException("Post not found", "POST_NOT_FOUND");
                });

        if (!post.getAuthor().getEmail().value().equals(userEmail)) {
            logger.warn("User with email: {} is not authorized to delete post with ID: {}", userEmail, postId.toString());
            throw new UnauthorizedException("User not authorized to delete this post", "USER_NOT_AUTHORIZED");
        }

        commentRepository.deleteByPost_PostId(postId);
        postRepository.deleteById(postId);
        logger.info("Post with ID: {} deleted successfully.", postId.toString());
    }

    public Page<PostRegisterResponse> getAllPosts(Pageable pageable) {
        if (pageable.isPaged()) {
            logger.info("Fetching all posts with pagination: page {}, size {}, sort {}.",
                    pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        } else {
            logger.info("Fetching all posts without pagination.");
        }
        Page<Post> postsPage = postRepository.findAll(pageable);
        return postsPage.map(postMapper::toResponse);
    }

    @Override
    public List<PostResponseDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::toPostResponseDTO)
                .collect(Collectors.toList());
    }
}
