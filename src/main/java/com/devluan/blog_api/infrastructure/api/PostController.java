package com.devluan.blog_api.infrastructure.api;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/new")
    public ResponseEntity<PostRegisterResponse> create(@RequestBody @Valid PostRegisterRequest request) {
        var response = postService.createPost(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostRegisterResponse> getById(@PathVariable UUID postId) {
        var postResponse = postService.getPostById(postId);
        return postResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostRegisterResponse> update(@PathVariable UUID postId, @RequestBody @Valid PostRegisterRequest request) {
        var updatedPost = postService.updatePost(postId, request);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
