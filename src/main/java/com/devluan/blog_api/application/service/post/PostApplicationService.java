package com.devluan.blog_api.application.service.post;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.application.dto.post.response.PostResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostApplicationService {
    PostRegisterResponse createPost(PostRegisterRequest request) throws java.io.IOException;
    Optional<PostRegisterResponse> getPostById(UUID postId);
    PostRegisterResponse updatePost(UUID postId, PostRegisterRequest request);
    void deletePost(UUID postId);
    Page<PostRegisterResponse> getAllPosts(Pageable pageable);
    List<PostResponseDTO> getAllPosts();
}