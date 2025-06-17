package com.devluan.blog_api.application.dto.response;

import java.util.UUID;

public record UserRegisterResponse(
        UUID userId,
        String username,
        String email
) {
}
