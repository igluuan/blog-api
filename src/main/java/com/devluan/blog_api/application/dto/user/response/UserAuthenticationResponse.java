package com.devluan.blog_api.application.dto.user.response;

public record UserAuthenticationResponse(
        String token,
        Long expireIn
) {
}
