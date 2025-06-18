package com.devluan.blog_api.domain.post.model;

import com.devluan.blog_api.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts_table")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User authorId;
    private String content;
    private String imgUrl;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private List<Comment> comments;
}
