package com.devluan.blog_api.domain.user.service.impl;

import com.devluan.blog_api.application.dto.token.TokenPair;
import com.devluan.blog_api.application.dto.user.request.UserAuthenticationRequest;
import com.devluan.blog_api.application.dto.user.request.UserRegisterRequest;
import com.devluan.blog_api.application.dto.user.response.UserAuthenticationResponse;
import com.devluan.blog_api.application.dto.user.response.UserRegisterResponse;
import com.devluan.blog_api.application.service.user.UserApplicationService;
import com.devluan.blog_api.domain.exception.DomainException;
import com.devluan.blog_api.domain.exception.InvalidCredentialsException;
import com.devluan.blog_api.domain.exception.InvalidUserDataException;
import com.devluan.blog_api.domain.exception.UserAlreadyExistsException;
import com.devluan.blog_api.domain.exception.UserNotFoundException;
import com.devluan.blog_api.domain.user.mapper.UserMapper;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.domain.user.valueObject.Email;
import com.devluan.blog_api.infrastructure.logger.LoggerService;
import com.devluan.blog_api.infrastructure.security.JwtTokenService;
import com.devluan.blog_api.infrastructure.security.TokenBlacklistService;
import com.devluan.blog_api.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserApplicationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LoggerService logger;
    private final JwtTokenService jwtTokenService;
    private final TokenBlacklistService tokenBlacklistService;
    private final FileStorageService fileStorageService;

    
    
        public enum ErrorCodes {
        NULL_REQUEST,
        EMAIL_ALREADY_EXISTS,
        DATA_INTEGRITY_VIOLATION,
        INTERNAL_ERROR;
    }

    public void createUser(UserRegisterRequest request) {
        if (request == null) {
            throw new InvalidUserDataException("Request cannot be null", ErrorCodes.NULL_REQUEST.name());
        }
        logger.info("Starting user registration process");
        try {
            validateRegistrationRequest(request);
            User user = buildUser(request);
            persistUser(user);
            logger.info("User registered successfully with email: {}", request.email());
        } catch (DomainException e) {
            logger.error("Domain error during user registration: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user registration for email: {}",
                    request.email(), e);
            throw new DomainException("Internal error during user registration",
                    ErrorCodes.INTERNAL_ERROR.name(), e);
        }
    }

    @Override
    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        if (request == null) {
            throw new InvalidUserDataException("Request cannot be null", ErrorCodes.NULL_REQUEST.name());
        }
        logger.info("Starting user registration process");
        try {
            validateRegistrationRequest(request);
            User user = buildUser(request);
            User savedUser = persistUser(user);
            logger.info("User registered successfully with email: {}", request.email());
            return userMapper.toResponseDTO(savedUser);
        } catch (DomainException e) {
            logger.error("Domain error during user registration: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during user registration for email: {}",
                    request.email(), e);
            throw new DomainException("Internal error during user registration",
                    ErrorCodes.INTERNAL_ERROR.name(), e);
        }
    }

    private void validateRegistrationRequest(UserRegisterRequest request) {
        try {
            request.validate();
            Email email = new Email(request.email());
            if (userRepository.existsByEmail(email)){
                throw new UserAlreadyExistsException(
                        "Email already registered: " + request.email(),
                        ErrorCodes.EMAIL_ALREADY_EXISTS.name()
                );
            }

            logger.debug("Request validation completed for email: {}", request.email());
        } catch (IllegalArgumentException e){
            throw new InvalidUserDataException("Invalid user data: " + e.getMessage(), ErrorCodes.NULL_REQUEST.name(), e);
        }

    }

    private User buildUser(UserRegisterRequest request) {
        logger.debug("Building user entity for email: {}", request.email());
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = userMapper.toEntity(request, encodedPassword);
        return user;
    }

    private User persistUser(User user) {
        try {
            User savedUser = userRepository.save(user);
            logger.debug("User persisted successfully: {}", savedUser.getUsername());
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            String email = user.getEmail().value();
            logger.warn("Attempt to register duplicate email: {}", email);
            throw new DomainException(
                    "Data integrity violation during user registration",
                    ErrorCodes.DATA_INTEGRITY_VIOLATION.name(),
                    e
            );
        }
    }

    @Override
    public UserAuthenticationResponse login(UserAuthenticationRequest request) {
        validateAuthenticationRequest(request);
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
        if (user.getRefreshToken() != null && jwtTokenService.isTokenValid(user.getRefreshToken())) {
            logger.info("Reusing existing refresh token for user: {}", user.getUsername());
            String newAccessToken = jwtTokenService.generateAccessTokenFromRefreshToken(user.getRefreshToken());
            user.assignAccessToken(newAccessToken, LocalDateTime.now().plusSeconds(jwtTokenService.getAccessTokenExpiresIn()));
            userRepository.save(user);
            return new UserAuthenticationResponse(newAccessToken, user.getRefreshToken(), jwtTokenService.getAccessTokenExpiresIn());
        }

        try {
            TokenPair tokenPair = jwtTokenService.generateTokens(user);
            user.assignRefreshToken(tokenPair.refreshToken(), LocalDateTime.now().plusSeconds(jwtTokenService.getRefreshTokenExpiresIn()));
            user.assignAccessToken(tokenPair.accessToken(), LocalDateTime.now().plusSeconds(jwtTokenService.getAccessTokenExpiresIn()));
            userRepository.save(user);
            logger.info("User logged in successfully: {}", user.getUsername());
            return new UserAuthenticationResponse(tokenPair.accessToken(), tokenPair.refreshToken(), tokenPair.expiresIn());
        } catch (DomainException e) {
            logger.error("Authentication error for user {}: {}", user.getEmail().value());
            throw new InvalidCredentialsException("Unexpected error during authentication.", "AUTHENTICATION_ERROR");
        }
    }

    @Override
    public UserAuthenticationResponse refreshAccessToken(String refreshToken) {
        if (!jwtTokenService.isRefreshToken(refreshToken) || !jwtTokenService.isTokenValid(refreshToken)) {
            throw new InvalidCredentialsException("Invalid refresh token", "INVALID_REFRESH_TOKEN");
        }

        String userEmail = jwtTokenService.getSubject(refreshToken);
        if (userEmail == null) {
            throw new InvalidCredentialsException("Invalid refresh token subject", "INVALID_REFRESH_TOKEN_SUBJECT");
        }

        Email emailObj = new Email(userEmail);
        User user = userRepository.findByEmail(emailObj)
                .orElseThrow(() -> new UserNotFoundException("User not found.", "USER_NOT_FOUND"));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new InvalidCredentialsException("Refresh token mismatch", "REFRESH_TOKEN_MISMATCH");
        }

        try {
            String newAccessToken = jwtTokenService.generateAccessTokenFromRefreshToken(refreshToken);
            user.assignAccessToken(newAccessToken, LocalDateTime.now().plusSeconds(jwtTokenService.getAccessTokenExpiresIn()));
            userRepository.save(user);
            return new UserAuthenticationResponse(newAccessToken, refreshToken, jwtTokenService.getAccessTokenExpiresIn());
        } catch (JwtException e) {
            throw new InvalidCredentialsException("Error refreshing access token", "REFRESH_TOKEN_ERROR");
        }
    }

    private void validateAuthenticationRequest(UserAuthenticationRequest request) {
        if (request == null || request.email() == null || request.password() == null) {
            throw new InvalidUserDataException("Invalid authentication request.", "INVALID_AUTH_REQUEST");
        }
        if (!request.email().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidUserDataException("Invalid email format.", "INVALID_EMAIL_FORMAT");
        }
    }

    @Override
    public void logout(String email) {
        Email emailObj = new Email(email);
        var user = userRepository.findByEmail(emailObj)
                .orElseThrow(() -> new UserNotFoundException("User not found.", "USER_NOT_FOUND"));
        if (user.getRefreshToken() != null) {
            tokenBlacklistService.blacklistToken(user.getRefreshToken());
        }
        if (user.getAccessToken() != null) {
            tokenBlacklistService.blacklistToken(user.getAccessToken());
        }
        user.clearRefreshToken();
        user.clearAccessToken();
        userRepository.save(user);
        logger.info("User logged out successfully: {}", email);
    }

    @Override
    public Optional<User> findUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional
    public User updateUser(UUID userId, UserRegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("User not found", "USER_NOT_FOUND"));

        user.updateUsername(request.username());
        user.updateEmail(new Email(request.email()));
        user.changePassword(passwordEncoder.encode(request.password()));

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new DomainException("User not found", "USER_NOT_FOUND");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void uploadProfileImage(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found", "USER_NOT_FOUND"));

        String imageUrl = fileStorageService.storeFile(file);
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
        logger.info("Profile image uploaded successfully for user: {}", userId);
    }

}
