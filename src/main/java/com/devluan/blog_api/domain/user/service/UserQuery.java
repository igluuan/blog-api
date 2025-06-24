package com.devluan.blog_api.domain.user.service;

import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQuery {
    private final UserRepository userRepository;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }
}