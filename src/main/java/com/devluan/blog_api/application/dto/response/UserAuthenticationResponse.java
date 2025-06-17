package com.devluan.blog_api.application.dto.response;

public record UserAuthenticationResponse(
        String token,
        Long expireIn
) {
}
