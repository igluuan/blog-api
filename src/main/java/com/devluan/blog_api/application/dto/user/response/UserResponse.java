package com.devluan.blog_api.application.dto.user.response;

import com.devluan.blog_api.application.dto.post.response.PostResponseDTO;

import java.util.List;
import java.util.UUID;

public record UserResponse(UUID id, String username, String email, List<PostResponseDTO> posts) {
}
