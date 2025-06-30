package com.devluan.blog_api.domain.user.repository;

import com.devluan.blog_api.domain.user.model.User;
import com.devluan.blog_api.domain.user.valueObject.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(Email email);
    Optional<User> findByEmail(Email email);
    Optional<User> findByUsername(String username);
}