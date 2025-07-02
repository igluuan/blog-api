package com.devluan.blog_api.application.dto.post.response;

public record PostRegisterResponse(
        java.util.UUID postId,
        String title,
        String content,
        String imgUrl,
        java.time.LocalDateTime createdAt,
        java.util.UUID authorId
) {
}
