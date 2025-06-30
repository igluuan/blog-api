package com.devluan.blog_api.domain.user.model;

import com.devluan.blog_api.domain.comment.model.Comment;
import com.devluan.blog_api.domain.post.model.Post;
import com.devluan.blog_api.domain.user.valueObject.Email;
import jakarta.persistence.*;
import com.devluan.blog_api.domain.auditable.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
public class User extends Auditable {

    public User(LocalDateTime createdAt, LocalDateTime updatedAt, UUID userId, String username, Email email, String password, String refreshToken, LocalDateTime refreshTokenExpiration, String accessToken, LocalDateTime accessTokenExpiration, List<Post> posts, List<Comment> comments) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.accessToken = accessToken;
        this.accessTokenExpiration = accessTokenExpiration;
        this.posts = posts;
        this.comments = comments;
    }

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

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "refresh_token_expiration")
    private LocalDateTime refreshTokenExpiration;

    @Column(columnDefinition = "TEXT")
    private String accessToken;

    @Column(name = "access_token_expiration")
    private LocalDateTime accessTokenExpiration;

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

    public void assignRefreshToken(String refreshToken, LocalDateTime expiration) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpiration = expiration;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiration = null;
    }

    public void assignAccessToken(String accessToken, LocalDateTime expiration) {
        this.accessToken = accessToken;
        this.accessTokenExpiration = expiration;
    }

    public void clearAccessToken() {
        this.accessToken = null;
        this.accessTokenExpiration = null;
    }
}