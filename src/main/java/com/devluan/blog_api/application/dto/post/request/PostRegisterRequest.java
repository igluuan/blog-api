package com.devluan.blog_api.application.dto.post.request;

import java.util.UUID;

public record PostRegisterRequest(String title, String content, UUID authorId) {
}
