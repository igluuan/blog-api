package com.devluan.blog_api.domain.user.mapper;

import com.devluan.blog_api.application.dto.user.request.UserRegisterRequest;
import com.devluan.blog_api.application.dto.user.response.UserRegisterResponse;
import com.devluan.blog_api.application.dto.user.response.UserResponse;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.valueObject.Email;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserRegisterResponse toResponseDTO(User user) {
        return new UserRegisterResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail() != null ? user.getEmail().getValue() : null
        );
    }

    public User toEntity(UserRegisterRequest request) {
        return new User(
                null,
                request.username(),
                new Email(request.email()),
                request.password(),
                null,
                null,
                null
        );
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail() != null ? user.getEmail().getValue() : null
        );
    }
}