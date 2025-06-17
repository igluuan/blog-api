package com.devluan.blog_api.infrastructure.api;

import com.devluan.blog_api.application.dto.request.UserAuthenticationRequest;
import com.devluan.blog_api.application.dto.request.UserRegisterRequest;
import com.devluan.blog_api.application.dto.response.UserAuthenticationResponse;
import com.devluan.blog_api.application.dto.response.UserRegisterResponse;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.domain.user.service.UserAuthentication;
import com.devluan.blog_api.domain.user.service.UserRegister;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRegister userRegister;
    private final UserAuthentication userAuthentication;
    private final UserRepository userRepository;

    @PostMapping("/new")
    public UserRegisterResponse create(@RequestBody @Valid UserRegisterRequest request){
        return userRegister.createUser(request);
    }
    @PostMapping("/login")
    public UserAuthenticationResponse login(@RequestBody @Valid UserAuthenticationRequest request){
        return userAuthentication.login(request);
    }
    @GetMapping("/{userId}")
    public Optional<User> getById(@PathVariable UUID userId){
        return userRepository.findById(userId);
    }
}
