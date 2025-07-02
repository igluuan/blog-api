package com.devluan.blog_api.application.dto.token;

public record TokenPair(String accessToken, String refreshToken, Long expiresIn) {
}