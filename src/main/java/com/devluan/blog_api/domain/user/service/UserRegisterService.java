package com.devluan.blog_api.domain.user.service;

import com.devluan.blog_api.application.dto.user.request.UserRegisterRequest;
import com.devluan.blog_api.application.dto.user.response.UserRegisterResponse;
import com.devluan.blog_api.domain.exception.DomainException;
import com.devluan.blog_api.domain.exception.InvalidUserDataException;
import com.devluan.blog_api.domain.exception.UserAlreadyExistsException;
import com.devluan.blog_api.domain.user.mapper.UserMapper;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.domain.user.valueObject.Email;
import com.devluan.blog_api.infrastructure.logger.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegisterService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LoggerService logger;

    public static final class ErrorCodes {
        public static final String NULL_REQUEST = "NULL_REQUEST";
        public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
        public static final String DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";
        public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    }

    @Transactional
    public UserRegisterResponse createUser(UserRegisterRequest request) {
        if (request == null) {
            throw new InvalidUserDataException("Request cannot be null", ErrorCodes.NULL_REQUEST);
        }
        logger.info("Starting user registration process");
        try {
            validateRequest(request);
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
                    ErrorCodes.INTERNAL_ERROR, e);
        }
    }

    private void validateRequest(UserRegisterRequest request) {
        try {
            request.validate();
            Email email = new Email(request.email());
            if (userRepository.existsByEmail(email)){
                throw new UserAlreadyExistsException(
                        "Email already registered: " + request.email(),
                        ErrorCodes.EMAIL_ALREADY_EXISTS
                );
            }

            logger.debug("Request validation completed for email: {}", request.email());
        } catch (IllegalArgumentException e){
            throw new InvalidUserDataException("Invalid user data: " + e.getMessage(), ErrorCodes.NULL_REQUEST, e);
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
                    ErrorCodes.DATA_INTEGRITY_VIOLATION,
                    e
            );
        }
    }

    @Transactional
    public User updateUser(UUID userId, UserRegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("User not found", "USER_NOT_FOUND"));

        user.updateUsername(request.username());
        user.updateEmail(new Email(request.email()));
        user.changePassword(passwordEncoder.encode(request.password()));

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new DomainException("User not found", "USER_NOT_FOUND");
        }
        userRepository.deleteById(userId);
    }
}