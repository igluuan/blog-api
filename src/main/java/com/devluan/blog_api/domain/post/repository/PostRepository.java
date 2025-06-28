package com.devluan.blog_api.domain.post.repository;

import com.devluan.blog_api.domain.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
}
