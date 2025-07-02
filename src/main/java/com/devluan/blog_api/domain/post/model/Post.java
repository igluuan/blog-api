package com.devluan.blog_api.domain.post.model;

import com.devluan.blog_api.domain.comment.model.Comment;
import com.devluan.blog_api.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    private String title;
    private String content;
    private String imgUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    public Post(UUID postId, User author, String title, String content, String imgUrl, List<Comment> comments) {
        this.postId = postId;
        this.author = author;
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.comments = comments;
    }

    public void updateTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        this.content = newContent;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}