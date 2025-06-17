package com.devluan.blog_api.domain.user.service;

import com.devluan.blog_api.application.dto.request.UserAuthenticationRequest;
import com.devluan.blog_api.application.dto.response.UserAuthenticationResponse;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.domain.user.service.logging.LoggerService;
import com.devluan.blog_api.infrastructure.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthentication {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final LoggerService logger;

    public UserAuthenticationResponse login(UserAuthenticationRequest request){
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Email not found."));
        if (!passwordEncoder.matches(request.password(), user.getPassword())){
            logger.warn("Invalid password for user with email: {}", request.email());
            throw new IllegalArgumentException("Invalid email or password");
        }
        try{
            long expiresIn = 300L;
            String token = jwtTokenService.generateToken(user, expiresIn);
            logger.info("user logged in successfully", user.getUsername());
            return new UserAuthenticationResponse(token, expiresIn);
        }catch (RuntimeException e){
            logger.error("error during authentication", user.getEmail(), e);
            throw new IllegalArgumentException("Unexpected error during authentication");
        }
    }
}
