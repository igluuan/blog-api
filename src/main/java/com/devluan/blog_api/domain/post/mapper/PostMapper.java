package com.devluan.blog_api.domain.post.mapper;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.application.dto.post.response.PostResponseDTO;
import com.devluan.blog_api.domain.post.model.Post;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class PostMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Post toEntity(PostRegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        return new Post(
                (java.util.UUID) null, // postId
                (com.devluan.blog_api.domain.user.model.User) null, // author (will be set later in service)
                request.title(),
                request.content(),
                request.imgUrl(),
                (java.util.List) null  // comments
        );
    }

    public PostRegisterResponse toResponse(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }
        return new PostRegisterResponse(post.getContent());
    }

    public PostResponseDTO toPostResponseDTO(Post post) {
        if (post == null) {
            return null;
        }
        return new PostResponseDTO(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getImgUrl(),
                post.getCreatedAt() != null ? post.getCreatedAt().format(FORMATTER) : null,
                post.getUpdatedAt() != null ? post.getUpdatedAt().format(FORMATTER) : null,
                post.getAuthor() != null ? post.getAuthor().getUsername() : null
        );
    }
}
