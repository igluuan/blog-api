package com.devluan.blog_api.application.dto.post.request;

public record PostRegisterRequest(String title, String content, UUID authorId) {
}
