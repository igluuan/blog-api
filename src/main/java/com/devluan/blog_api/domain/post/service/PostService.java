package com.devluan.blog_api.domain.post.service;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.domain.post.mapper.PostMapper;
import com.devluan.blog_api.domain.post.model.Post;
import com.devluan.blog_api.domain.post.repository.PostRepository;
import com.devluan.blog_api.domain.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostRegisterResponse createPost(PostRegisterRequest request){
        if (request == null){
            throw new IllegalArgumentException("Post cannot be null");
        }
        Post newPost = postMapper.toEntity(request);
        postRepository.save(newPost);
        return postMapper.toResponse(newPost);
    }

    public Optional<PostRegisterResponse> getPostById(UUID postId) {
        return postRepository.findById(postId)
                .map(postMapper::toResponse);
    }

    public PostRegisterResponse updatePost(UUID postId, PostRegisterRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DomainException("Post not found", "POST_NOT_FOUND"));

        post.updateTitle(request.title());
        post.updateContent(request.content());

        Post updatedPost = postRepository.save(post);
        return postMapper.toResponse(updatedPost);
    }

    public void deletePost(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new DomainException("Post not found", "POST_NOT_FOUND");
        }
        postRepository.deleteById(postId);
    }
}
