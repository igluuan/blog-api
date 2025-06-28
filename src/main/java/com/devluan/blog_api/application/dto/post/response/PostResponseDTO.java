package com.devluan.blog_api.application.dto.post.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponseDTO(UUID postId, String title, String content, String imgUrl, LocalDateTime createdAt) {
}
