package com.devluan.blog_api.domain.comment.repository;

import com.devluan.blog_api.domain.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
}
