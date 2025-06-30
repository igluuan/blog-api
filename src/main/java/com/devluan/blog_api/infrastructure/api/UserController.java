package com.devluan.blog_api.infrastructure.api;

import com.devluan.blog_api.application.dto.user.request.EmailRequest;
import com.devluan.blog_api.application.dto.user.request.UserAuthenticationRequest;
import com.devluan.blog_api.application.dto.user.request.UserRegisterRequest;
import com.devluan.blog_api.application.dto.user.response.UserAuthenticationResponse;
import com.devluan.blog_api.application.dto.user.response.UserRegisterResponse;
import com.devluan.blog_api.application.dto.user.response.UserResponse;
import com.devluan.blog_api.domain.user.mapper.UserMapper;
import com.devluan.blog_api.application.service.user.UserApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserApplicationService userApplicationService;
    private final UserMapper userMapper;

    @PostMapping("/new")
    public ResponseEntity<UserRegisterResponse> create(@RequestBody @Valid UserRegisterRequest request) {
        var response = userApplicationService.registerUser(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthenticationResponse> login(@RequestBody @Valid UserAuthenticationRequest request) {
        var response = userApplicationService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID userId) {
        var userResponse = userApplicationService.findUserById(userId);
        return userResponse
                .map(user -> ResponseEntity.ok(userMapper.toUserResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID userId, @RequestBody @Valid UserRegisterRequest request) {
        var updatedUser = userApplicationService.updateUser(userId, request);
        return ResponseEntity.ok(userMapper.toUserResponse(updatedUser));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userApplicationService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody EmailRequest request) {
        userApplicationService.logout(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<UserAuthenticationResponse> refreshToken(@RequestBody String refreshToken) {
        var response = userApplicationService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}