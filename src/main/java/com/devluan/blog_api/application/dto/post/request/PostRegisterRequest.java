package com.devluan.blog_api.application.dto.post.request;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public record PostRegisterRequest(String title, String content, MultipartFile image, UUID authorId) {
}
