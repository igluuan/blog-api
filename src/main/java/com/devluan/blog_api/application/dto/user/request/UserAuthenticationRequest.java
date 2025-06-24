package com.devluan.blog_api.application.dto.user.request;

public record UserAuthenticationRequest(
        String email,
        String password
) {
}
