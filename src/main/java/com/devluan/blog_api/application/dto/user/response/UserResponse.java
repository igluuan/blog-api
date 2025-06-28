package com.devluan.blog_api.application.dto.user.response;

import java.util.UUID;

public record UserResponse(UUID id, String username, String email) {
}
