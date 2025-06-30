package com.devluan.blog_api.application.service.user;

import com.devluan.blog_api.application.dto.user.request.UserAuthenticationRequest;
import com.devluan.blog_api.application.dto.user.request.UserRegisterRequest;
import com.devluan.blog_api.application.dto.user.response.UserAuthenticationResponse;
import com.devluan.blog_api.application.dto.user.response.UserRegisterResponse;
import com.devluan.blog_api.domain.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserApplicationService {
    void createUser(UserRegisterRequest request);
    UserRegisterResponse registerUser(UserRegisterRequest request);
    UserAuthenticationResponse login(UserAuthenticationRequest request);
    UserAuthenticationResponse refreshAccessToken(String refreshToken);
    void logout(String request);
    Optional<User> findUserById(UUID userId);
    User updateUser(UUID userId, UserRegisterRequest request);
    void deleteUser(UUID userId);
    Optional<User> findUserByUsername(String username);
}