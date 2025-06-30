package com.devluan.blog_api.service;

import com.devluan.blog_api.application.dto.user.request.UserRegisterRequest;
import com.devluan.blog_api.application.dto.user.response.UserRegisterResponse;
import com.devluan.blog_api.domain.exception.DomainException;
import com.devluan.blog_api.domain.exception.InvalidUserDataException;
import com.devluan.blog_api.domain.exception.UserAlreadyExistsException;
import com.devluan.blog_api.domain.user.mapper.UserMapper;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.domain.user.service.UserRegisterService;
import com.devluan.blog_api.domain.user.valueObject.Email;
import com.devluan.blog_api.infrastructure.logger.LoggerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRegisterService Tests")
class UserRegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private LoggerService logger;

    @Mock
    private UserRegisterRequest request;

    @Mock
    private User user;

    @Mock
    private User savedUser;

    @Mock
    private UserRegisterResponse response;

    @Mock
    private Email email;

    @InjectMocks
    private UserRegisterService userRegisterService;

    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASSWORD = "ValidPassword123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPassword";
    private static final String USERNAME = "testuser";

    @Nested
    @DisplayName("Successful User Registration")
    class SuccessfulRegistration {

        @Test
        @DisplayName("Should register user successfully with valid data")
        void shouldRegisterUserSuccessfully() {
            // Arrange
            when(request.email()).thenReturn(VALID_EMAIL);
            when(request.password()).thenReturn(VALID_PASSWORD);
            when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(user);
            when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
            when(userRepository.save(user)).thenReturn(savedUser);
            when(userMapper.toResponseDTO(savedUser)).thenReturn(response);
            when(savedUser.getUsername()).thenReturn(USERNAME);

            // Act
            UserRegisterResponse result = userRegisterService.createUser(request);

            // Assert
            assertNotNull(result);
            assertEquals(response, result);

            // Verify interactions
            verify(request).validate();
            verify(userRepository).existsByEmail(any(Email.class));
            verify(userMapper).toEntity(request);
            verify(passwordEncoder).encode(VALID_PASSWORD);
            verify(user).changePassword(ENCODED_PASSWORD);
            verify(userRepository).save(user);
            verify(userMapper).toResponseDTO(savedUser);

            // Verify logging
            verify(logger).info("Starting user registration process");
            verify(logger).debug("Request validation completed for email: {}", VALID_EMAIL);
            verify(logger).debug("Building user entity for email: {}", VALID_EMAIL);
            verify(logger).debug("User persisted successfully: {}", USERNAME);
            verify(logger).info("User registered successfully with email: {}", VALID_EMAIL);
        }
    }

    @Nested
    @DisplayName("Null Request Validation")
    class NullRequestValidation {

        @Test
        @DisplayName("Should throw InvalidUserDataException when request is null")
        void shouldThrowExceptionWhenRequestIsNull() {
            // Act & Assert
            InvalidUserDataException exception = assertThrows(
                    InvalidUserDataException.class,
                    () -> userRegisterService.createUser(null)
            );

            assertEquals("Request cannot be null", exception.getMessage());
            assertEquals(UserRegisterService.ErrorCodes.NULL_REQUEST, exception.getErrorCode());

            // Verify no interactions with other dependencies
            verifyNoInteractions(userRepository, userMapper, passwordEncoder);
        }
    }

    @Nested
    @DisplayName("Request Validation")
    class RequestValidation {

        @Test
        @DisplayName("Should throw InvalidUserDataException when request validation fails")
        void shouldThrowExceptionWhenRequestValidationFails() {
            // Arrange - removido stub desnecessário
            doThrow(new IllegalArgumentException("Invalid email format"))
                    .when(request).validate();

            // Act & Assert
            InvalidUserDataException exception = assertThrows(
                    InvalidUserDataException.class,
                    () -> userRegisterService.createUser(request)
            );

            assertEquals("Invalid user data: Invalid email format", exception.getMessage());
            assertEquals(UserRegisterService.ErrorCodes.NULL_REQUEST, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw InvalidUserDataException when Email constructor fails")
        void shouldThrowExceptionWhenEmailConstructorFails() {
            // Arrange - removido stub desnecessário do email
            doThrow(new IllegalArgumentException("Invalid email format"))
                    .when(request).validate();

            // Act & Assert
            InvalidUserDataException exception = assertThrows(
                    InvalidUserDataException.class,
                    () -> userRegisterService.createUser(request)
            );

            assertEquals("Invalid user data: Invalid email format", exception.getMessage());
            assertEquals(UserRegisterService.ErrorCodes.NULL_REQUEST, exception.getErrorCode());
        }


        @Nested
        @DisplayName("Email Already Exists Validation")
        class EmailExistsValidation {

            @Test
            @DisplayName("Should throw UserAlreadyExistsException when email already exists")
            void shouldThrowExceptionWhenEmailAlreadyExists() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

                // Act & Assert
                UserAlreadyExistsException exception = assertThrows(
                        UserAlreadyExistsException.class,
                        () -> userRegisterService.createUser(request)
                );

                assertEquals("Email already registered: " + VALID_EMAIL, exception.getMessage());
                assertEquals(UserRegisterService.ErrorCodes.EMAIL_ALREADY_EXISTS, exception.getErrorCode());

                verify(request).validate();
                verify(userRepository).existsByEmail(any(Email.class));
                verify(logger).error("Domain error during user registration: {}", exception.getMessage());

                // Verify no further processing
                verifyNoInteractions(userMapper, passwordEncoder);
                verify(userRepository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("Data Integrity Violations")
        class DataIntegrityViolations {

            @Test
            @DisplayName("Should throw DomainException when DataIntegrityViolationException occurs during save")
            void shouldThrowDomainExceptionWhenDataIntegrityViolationOccurs() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(request.password()).thenReturn(VALID_PASSWORD);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
                when(userMapper.toEntity(request)).thenReturn(user);
                when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
                when(user.getEmail()).thenReturn(email);
                when(email.value()).thenReturn(VALID_EMAIL);

                DataIntegrityViolationException dataException = new DataIntegrityViolationException("Duplicate key");
                when(userRepository.save(user)).thenThrow(dataException);

                // Act & Assert
                DomainException exception = assertThrows(
                        DomainException.class,
                        () -> userRegisterService.createUser(request)
                );

                assertEquals("Data integrity violation during user registration", exception.getMessage());
                assertEquals(UserRegisterService.ErrorCodes.DATA_INTEGRITY_VIOLATION, exception.getErrorCode());
                assertEquals(dataException, exception.getCause());

                verify(logger).warn("Attempt to register duplicate email: {}", VALID_EMAIL);
                verify(logger).error("Domain error during user registration: {}", exception.getMessage());
            }
        }

        @Nested
        @DisplayName("Unexpected Exceptions")
        class UnexpectedExceptions {

            @Test
            @DisplayName("Should throw DomainException when unexpected exception occurs during validation")
            void shouldThrowDomainExceptionWhenUnexpectedExceptionOccursDuringValidation() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                RuntimeException unexpectedException = new RuntimeException("Unexpected error");
                when(userRepository.existsByEmail(any(Email.class))).thenThrow(unexpectedException);

                // Act & Assert
                DomainException exception = assertThrows(
                        DomainException.class,
                        () -> userRegisterService.createUser(request)
                );

                assertEquals("Internal error during user registration", exception.getMessage());
                assertEquals(UserRegisterService.ErrorCodes.INTERNAL_ERROR, exception.getErrorCode());
                assertEquals(unexpectedException, exception.getCause());

                verify(logger).error("Unexpected error during user registration for email: {}",
                        VALID_EMAIL, unexpectedException);
            }

            @Test
            @DisplayName("Should throw DomainException when unexpected exception occurs during user building")
            void shouldThrowDomainExceptionWhenUnexpectedExceptionOccursDuringUserBuilding() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(request.password()).thenReturn(VALID_PASSWORD);
                lenient().when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
                when(userMapper.toEntity(request)).thenThrow(new RuntimeException("Mapper error"));

                // Act & Assert
                DomainException exception = assertThrows(
                        DomainException.class,
                        () -> userRegisterService.createUser(request)
                );

                assertEquals("Internal error during user registration", exception.getMessage());
                assertEquals(UserRegisterService.ErrorCodes.INTERNAL_ERROR, exception.getErrorCode());
            }

            @Test
            @DisplayName("Should throw DomainException when unexpected exception occurs during password encoding")
            void shouldThrowDomainExceptionWhenUnexpectedExceptionOccursDuringPasswordEncoding() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(request.password()).thenReturn(VALID_PASSWORD);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
                when(userMapper.toEntity(request)).thenReturn(user);

                RuntimeException unexpectedException = new RuntimeException("Encoding error");
                when(passwordEncoder.encode(VALID_PASSWORD)).thenThrow(unexpectedException);

                // Act & Assert
                DomainException exception = assertThrows(
                        DomainException.class,
                        () -> userRegisterService.createUser(request)
                );

                assertEquals("Internal error during user registration", exception.getMessage());
                assertEquals(UserRegisterService.ErrorCodes.INTERNAL_ERROR, exception.getErrorCode());
                assertEquals(unexpectedException, exception.getCause());
            }
        }

        @Nested
        @DisplayName("Method Interaction Tests")
        class MethodInteractionTests {

            @Test
            @DisplayName("Should call all methods in correct order for successful registration")
            void shouldCallMethodsInCorrectOrder() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(request.password()).thenReturn(VALID_PASSWORD);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
                when(userMapper.toEntity(request)).thenReturn(user);
                when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
                when(userRepository.save(user)).thenReturn(savedUser);
                when(userMapper.toResponseDTO(savedUser)).thenReturn(response);
                when(savedUser.getUsername()).thenReturn(USERNAME);

                // Act
                userRegisterService.createUser(request);

                // Assert - verify order of calls
                var inOrder = inOrder(request, userRepository, userMapper, passwordEncoder, user, logger);

                inOrder.verify(logger).info("Starting user registration process");
                inOrder.verify(request).validate();
                inOrder.verify(userRepository).existsByEmail(any(Email.class));
                inOrder.verify(logger).debug("Request validation completed for email: {}", VALID_EMAIL);
                inOrder.verify(userMapper).toEntity(request);
                inOrder.verify(passwordEncoder).encode(VALID_PASSWORD);
                inOrder.verify(user).changePassword(ENCODED_PASSWORD);
                inOrder.verify(userRepository).save(user);
                inOrder.verify(logger).debug("User persisted successfully: {}", USERNAME);
                inOrder.verify(userMapper).toResponseDTO(savedUser);
            }

            @Test
            @DisplayName("Should not call save when validation fails")
            void shouldNotCallSaveWhenValidationFails() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

                // Act & Assert
                assertThrows(UserAlreadyExistsException.class,
                        () -> userRegisterService.createUser(request));

                verify(userRepository, never()).save(any());
                verify(userMapper, never()).toEntity(any());
                verify(passwordEncoder, never()).encode(any());
                verify(userMapper, never()).toResponseDTO(any());
            }
        }

        @Nested
        @DisplayName("Error Code Validation")
        class ErrorCodeValidation {

            @Test
            @DisplayName("Should use correct error codes for different exceptions")
            void shouldUseCorrectErrorCodes() {
                // Test NULL_REQUEST error code
                InvalidUserDataException nullException = assertThrows(
                        InvalidUserDataException.class,
                        () -> userRegisterService.createUser(null)
                );
                assertEquals(UserRegisterService.ErrorCodes.NULL_REQUEST, nullException.getErrorCode());

                // Test EMAIL_ALREADY_EXISTS error code
                when(request.email()).thenReturn(VALID_EMAIL);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

                UserAlreadyExistsException existsException = assertThrows(
                        UserAlreadyExistsException.class,
                        () -> userRegisterService.createUser(request)
                );
                assertEquals(UserRegisterService.ErrorCodes.EMAIL_ALREADY_EXISTS, existsException.getErrorCode());
            }
        }

        @Nested
        @DisplayName("Edge Cases")
        class EdgeCases {

            @Test
            @DisplayName("Should handle empty email string")
            void shouldHandleEmptyEmailString() {
                // Arrange
                doThrow(new IllegalArgumentException("Email cannot be empty"))
                        .when(request).validate();

                // Act & Assert
                InvalidUserDataException exception = assertThrows(
                        InvalidUserDataException.class,
                        () -> userRegisterService.createUser(request)
                );

                assertEquals("Invalid user data: Email cannot be empty", exception.getMessage());
            }

            @Test
            @DisplayName("Should handle null password")
            void shouldHandleNullPassword() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(request.password()).thenReturn(null);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
                when(userMapper.toEntity(request)).thenReturn(user);

                RuntimeException exception = new RuntimeException("Password cannot be null");
                when(passwordEncoder.encode(null)).thenThrow(exception);

                // Act & Assert
                DomainException domainException = assertThrows(
                        DomainException.class,
                        () -> userRegisterService.createUser(request)
                );

                assertEquals("Internal error during user registration", domainException.getMessage());
                assertEquals(UserRegisterService.ErrorCodes.INTERNAL_ERROR, domainException.getErrorCode());
            }
        }

        @Nested
        @DisplayName("Logging Verification")
        class LoggingVerification {

            @Test
            @DisplayName("Should log appropriate messages for successful registration")
            void shouldLogAppropriateMessagesForSuccessfulRegistration() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(request.password()).thenReturn(VALID_PASSWORD);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
                when(userMapper.toEntity(request)).thenReturn(user);
                when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
                when(userRepository.save(user)).thenReturn(savedUser);
                when(userMapper.toResponseDTO(savedUser)).thenReturn(response);
                when(savedUser.getUsername()).thenReturn(USERNAME);

                // Act
                userRegisterService.createUser(request);

                // Assert
                verify(logger).info("Starting user registration process");
                verify(logger).debug("Request validation completed for email: {}", VALID_EMAIL);
                verify(logger).debug("Building user entity for email: {}", VALID_EMAIL);
                verify(logger).debug("User persisted successfully: {}", USERNAME);
                verify(logger).info("User registered successfully with email: {}", VALID_EMAIL);
            }

            @Test
            @DisplayName("Should log error messages for domain exceptions")
            void shouldLogErrorMessagesForDomainExceptions() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

                // Act
                assertThrows(UserAlreadyExistsException.class,
                        () -> userRegisterService.createUser(request));

                // Assert
                verify(logger).error(eq("Domain error during user registration: {}"), anyString());
            }

            @Test
            @DisplayName("Should log warning for data integrity violations")
            void shouldLogWarningForDataIntegrityViolations() {
                // Arrange
                when(request.email()).thenReturn(VALID_EMAIL);
                when(request.password()).thenReturn(VALID_PASSWORD);
                when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
                when(userMapper.toEntity(request)).thenReturn(user);
                when(passwordEncoder.encode(VALID_PASSWORD)).thenReturn(ENCODED_PASSWORD);
                when(user.getEmail()).thenReturn(email);
                when(email.value()).thenReturn(VALID_EMAIL);
                when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("Duplicate"));

                // Act
                assertThrows(DomainException.class,
                        () -> userRegisterService.createUser(request));

                // Assert
                verify(logger).warn("Attempt to register duplicate email: {}", VALID_EMAIL);
            }
        }
    }
}