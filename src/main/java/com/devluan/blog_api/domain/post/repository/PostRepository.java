package com.devluan.blog_api.domain.post.repository;

import com.devluan.blog_api.domain.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
