package com.devluan.blog_api.application.dto.user.response;

public record UserAuthenticationResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn
) {
}
