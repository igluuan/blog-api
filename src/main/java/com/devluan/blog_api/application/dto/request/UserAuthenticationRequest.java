package com.devluan.blog_api.application.dto.request;

public record UserAuthenticationRequest(
        String email,
        String password
) {
}
