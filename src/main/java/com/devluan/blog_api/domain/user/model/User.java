package com.devluan.blog_api.domain.user.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users_table")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    @Column(nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public void changeEmail(String newEmail){
        if (newEmail == null){
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = newEmail;
    }
    public void changePassword(String newPassword, BCryptPasswordEncoder passwordEncoder){
        if(newPassword == null){
            throw new IllegalArgumentException("Password cannot be null");
        }
        this.password = passwordEncoder.encode(newPassword);
    }
}
