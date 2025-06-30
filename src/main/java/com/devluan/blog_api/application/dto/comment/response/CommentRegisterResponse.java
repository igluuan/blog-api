package com.devluan.blog_api.application.dto.comment.response;

import java.util.UUID;

public record CommentRegisterResponse(UUID commentId, String content, UUID authorId, UUID postId, String createdAt, String updatedAt) {
}
