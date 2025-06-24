package com.devluan.blog_api.application.dto.user.response;

import java.util.UUID;

public record UserRegisterResponse(
        UUID userId,
        String username,
        String email
) {
}
