package com.devluan.blog_api.application.dto.comment.request;

import java.util.UUID;

public record CommentRegisterRequest(String content, UUID authorId, UUID postId) {
}
