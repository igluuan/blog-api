package com.devluan.blog_api.domain.post.service;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.domain.post.mapper.PostMapper;
import com.devluan.blog_api.domain.post.model.Post;
import com.devluan.blog_api.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
