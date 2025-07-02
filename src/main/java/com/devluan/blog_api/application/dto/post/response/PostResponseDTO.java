package com.devluan.blog_api.application.dto.post.response;

import java.util.UUID;

public record PostResponseDTO(UUID postId, String title, String content, String imgUrl, String createdAt, String updatedAt, String authorUsername, String authorProfileImageUrl) {
}
