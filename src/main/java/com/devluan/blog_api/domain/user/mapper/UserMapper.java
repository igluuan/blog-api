package com.devluan.blog_api.domain.user.mapper;

import com.devluan.blog_api.application.dto.request.UserRegisterRequest;
import com.devluan.blog_api.application.dto.response.UserRegisterResponse;
import com.devluan.blog_api.domain.user.model.User;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public User toEntity(UserRegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("UserRegisterRequest cannot be null");
        }

        return new User(null, request.username(), request.email(), request.password(), LocalDateTime.now());
    }

    public UserRegisterRequest toDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return new UserRegisterRequest(
                user.getEmail(),
                user.getUsername(),
                null
        );
    }

    public UserRegisterResponse toResponseDTO(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return new UserRegisterResponse(
                user.getUserId(),
                user.getEmail(),
                user.getUsername()
        );
    }
}