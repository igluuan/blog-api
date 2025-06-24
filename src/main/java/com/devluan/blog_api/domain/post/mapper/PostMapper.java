package com.devluan.blog_api.domain.post.mapper;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.domain.post.model.Post;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostMapper {
    public Post toEntity(PostRegisterRequest request){
        if (request == null){
            throw new IllegalArgumentException("Request cannot be null");
        }
        return new Post(null,
                null,
                request.content(),
                null,
                LocalDateTime.now(),
                null );
    }
    public PostRegisterResponse toResponse(Post post){
        if (post == null){
            throw new IllegalArgumentException("Post cannot be null");
        }
        return new PostRegisterResponse(post.getContent());
    }
}
