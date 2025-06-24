package com.devluan.blog_api.infrastructure.api;

import com.devluan.blog_api.application.dto.user.request.UserAuthenticationRequest;
import com.devluan.blog_api.application.dto.user.request.UserRegisterRequest;
import com.devluan.blog_api.application.dto.user.response.UserAuthenticationResponse;
import com.devluan.blog_api.application.dto.user.response.UserRegisterResponse;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.service.UserAuthentication;
import com.devluan.blog_api.domain.user.service.UserQuery;
import com.devluan.blog_api.domain.user.service.UserRegister;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRegister userRegister;
    private final UserAuthentication userAuthentication;
    private final UserQuery userQuery;

    @PostMapping("/new")
    public ResponseEntity<UserRegisterResponse> create(@RequestBody @Valid UserRegisterRequest request) {
        var response = userRegister.createUser(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthenticationResponse> login(@RequestBody @Valid UserAuthenticationRequest request) {
        var response = userAuthentication.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getById(@PathVariable UUID userId) {
        var userResponse = userQuery.findById(userId);
        return userResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}