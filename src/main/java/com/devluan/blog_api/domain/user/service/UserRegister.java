package com.devluan.blog_api.domain.user.service;

import com.devluan.blog_api.application.dto.user.response.UserRegisterResponse;
import com.devluan.blog_api.domain.exception.DomainException;
import com.devluan.blog_api.domain.exception.InvalidUserDataException;
import com.devluan.blog_api.domain.exception.UserAlreadyExistsException;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.application.dto.user.request.UserRegisterRequest;
import com.devluan.blog_api.domain.user.mapper.UserMapper;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.domain.user.valueObject.Email;
import com.devluan.blog_api.infrastructure.logger.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegister {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LoggerService logger;

    public UserRegisterResponse createUser(UserRegisterRequest request) {
        validateRequest(request);
        checkEmailExists(request.email());
        User user = prepareUser(request);
        User savedUser = saveUser(user);
        return userMapper.toResponseDTO(savedUser);
    }

    private void validateRequest(UserRegisterRequest request) {
        if (request == null) {
            throw new InvalidUserDataException("User cannot be null.", "INVALID_USER_DATA");
        }
        request.validate();
    }

    private void checkEmailExists(String email) {
        Email emailObj = new Email(email);
        if (userRepository.existsByEmail(emailObj)) {
            throw new UserAlreadyExistsException("Error when creating account, email already has an account.", "USER_ALREADY_EXISTS");
        }
    }

    private User prepareUser(UserRegisterRequest request) {
        logger.info(String.format("Creating user with email: %s", request.email()), null);
        User user = userMapper.toEntity(request);
        user.changePassword(request.password(), passwordEncoder);
        return user;
    }

    private User saveUser(User user) {
        try {
            User savedUser = userRepository.save(user);
            logger.info(String.format("User saved successfully: %s", savedUser.getUsername()), null);
            return savedUser;
        } catch (UserAlreadyExistsException e) {
            logger.error(String.format("User with email %s already exists", user.getEmail().value()), null);
            throw new DomainException("Email already registered", e.getErrorCode(), e);
        } catch (InvalidUserDataException e) {
            logger.error(String.format("Invalid user data for email %s", user.getEmail().value()), null);
            throw new DomainException("Invalid data: " + e.getMessage(), e.getErrorCode(), e);
        } catch (DataIntegrityViolationException e) {
            logger.error(String.format("Database integrity violation for user %s", user.getEmail().value()), null);
            throw new DomainException("Error saving user: email or another field already exists", "DATA_INTEGRITY_VIOLATION", e);
        } catch (Exception e) {
            logger.error(String.format("Unexpected error while saving user %s", user.getEmail().value()), null);
            throw new DomainException("Internal error while saving user", "INTERNAL_SERVER_ERROR", e);
        }
    }
}
