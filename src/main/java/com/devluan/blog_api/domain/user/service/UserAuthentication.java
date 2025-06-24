package com.devluan.blog_api.domain.user.service;

import com.devluan.blog_api.application.dto.user.request.UserAuthenticationRequest;
import com.devluan.blog_api.application.dto.user.response.UserAuthenticationResponse;
import com.devluan.blog_api.domain.exception.DomainException;
import com.devluan.blog_api.domain.exception.InvalidCredentialsException;
import com.devluan.blog_api.domain.exception.InvalidUserDataException;
import com.devluan.blog_api.domain.exception.UserNotFoundException;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.domain.user.valueObject.Email;
import com.devluan.blog_api.infrastructure.logger.LoggerService;
import com.devluan.blog_api.infrastructure.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthentication {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final LoggerService logger;
    @Value("${jwt.token.expiration}")
    private long expiresIn;

    public UserAuthenticationResponse login(UserAuthenticationRequest request) {
        validateRequest(request);
        Email emailObj = new Email(request.email());
        var user = userRepository.findByEmail(emailObj)
                .orElseThrow(() -> new UserNotFoundException("User not found.", "USER_NOT_FOUND"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            logger.warn("Failed login attempt for email: {}", request.email());
            throw new InvalidCredentialsException("Invalid email or password.", "INVALID_CREDENTIALS");
        }
        return generateAuthenticationResponse(user);
    }

    private UserAuthenticationResponse generateAuthenticationResponse(User user) {
        try {
            String token = jwtTokenService.generateToken(user, expiresIn);
            logger.info("User logged in successfully: {}", user.getUsername());
            return new UserAuthenticationResponse(token, expiresIn);
        } catch (DomainException e) {
            logger.error("Authentication error for user {}: {}", user.getEmail().value());
            throw new InvalidCredentialsException("Unexpected error during authentication.", "AUTHENTICATION_ERROR");
        }
    }

    private void validateRequest(UserAuthenticationRequest request) {
        if (request == null || request.email() == null || request.password() == null) {
            throw new InvalidUserDataException("Invalid authentication request.", "INVALID_AUTH_REQUEST");
        }
        if (!request.email().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidUserDataException("Invalid email format.", "INVALID_EMAIL_FORMAT");
        }
    }
}
