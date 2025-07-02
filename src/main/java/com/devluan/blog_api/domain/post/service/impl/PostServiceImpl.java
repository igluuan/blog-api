package com.devluan.blog_api.domain.post.service.impl;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.application.service.post.PostApplicationService;
import com.devluan.blog_api.application.dto.post.response.PostResponseDTO;
import com.devluan.blog_api.domain.post.mapper.PostMapper;
import com.devluan.blog_api.domain.post.model.Post;
import com.devluan.blog_api.domain.post.repository.PostRepository;
import com.devluan.blog_api.domain.exception.DomainException;
import com.devluan.blog_api.domain.exception.UserNotFoundException;
import com.devluan.blog_api.domain.exception.PostNotFoundException;
import com.devluan.blog_api.domain.exception.InvalidPostDataException;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.infrastructure.logger.LoggerService;
import com.devluan.blog_api.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final FileStorageService fileStorageService;

    public PostRegisterResponse createPost(PostRegisterRequest request) throws IOException {
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

        if (request.image() != null && !request.image().isEmpty()) {
            String imageUrl = fileStorageService.storeFile(request.image());
            newPost.setImgUrl(imageUrl);
        }

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
                    return new PostNotFoundException("Post not found", "POST_NOT_FOUND");
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
            throw new PostNotFoundException("Post not found", "POST_NOT_FOUND");
        }
        postRepository.deleteById(postId);
        logger.info("Post with ID: {} deleted successfully.", postId.toString());
    }

    public Page<PostRegisterResponse> getAllPosts(Pageable pageable) {
        logger.info(String.format("Fetching all posts with pagination: page %d, size %d, sort %s.",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()));
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
