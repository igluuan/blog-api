package com.devluan.blog_api.domain.user.service;

import com.devluan.blog_api.application.dto.response.UserRegisterResponse;
import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.application.dto.request.UserRegisterRequest;
import com.devluan.blog_api.domain.user.mapper.UserMapper;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import com.devluan.blog_api.domain.user.service.logging.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegister {

    private final UserRepository userRepository;
    private  final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LoggerService logger;

    public UserRegisterResponse createUser(UserRegisterRequest request){
        if (request == null){
            throw new IllegalArgumentException("Usuário nulo.");
        }
        request.validate();
        if (userRepository.existsByEmail(request.email())){
            throw new IllegalStateException("Email está em uso");
        }
        try {
            logger.info("creating user with email: {}", request.email());
            System.out.print(request.password());
            User user = userMapper.toEntity(request);
            System.out.print(user.getPassword());
            user.changePassword(request.password(), passwordEncoder);
            System.out.print(user.getPassword());
            User savedUser = userRepository.save(user);
            logger.info("user saved successfully", savedUser.getUsername());
            return userMapper.toResponseDTO(savedUser);
        } catch (RuntimeException e) {
            logger.error("Failed to create user with email: {}", request.email(), e);
            throw new RuntimeException(e);
        }
    }
}
