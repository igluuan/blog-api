package com.devluan.blog_api.domain.user.model;

import com.devluan.blog_api.domain.comment.model.Comment;
import com.devluan.blog_api.domain.post.model.Post;
import com.devluan.blog_api.domain.user.valueObject.Email;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false)
    private String username;

    @Embedded
    @Column(unique = true, nullable = false)
    private Email email;

    @Column(nullable = false)
    private String password;

    @Column(name = "access_token", length = 500)
    private String accessToken;

    @Column(name = "access_token_expiration")
    private LocalDateTime accessTokenExpiration;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "refresh_token_expiration")
    private LocalDateTime refreshTokenExpiration;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "author")
    private List<Post> posts;

    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    public void changeEmail(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = newEmail;
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and cannot be null");
        }
        this.password = newPassword;
    }

    public void updateUsername(String newUsername) {
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = newUsername;
    }

    public void updateEmail(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.email = newEmail;
    }

    public void assignAccessToken(String accessToken, LocalDateTime expiration) {
        this.accessToken = accessToken;
        this.accessTokenExpiration = expiration;
    }

    public void clearAccessToken() {
        this.accessToken = null;
        this.accessTokenExpiration = null;
    }

    public void assignRefreshToken(String refreshToken, LocalDateTime expiration) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiration = expiration;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiration = null;
    }
}