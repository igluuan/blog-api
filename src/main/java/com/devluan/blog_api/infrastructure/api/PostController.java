package com.devluan.blog_api.infrastructure.api;

import com.devluan.blog_api.application.dto.post.request.PostRegisterRequest;
import com.devluan.blog_api.application.dto.post.response.PostRegisterResponse;
import com.devluan.blog_api.application.service.post.PostApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostApplicationService postApplicationService;

    @PostMapping("/new")
    public ResponseEntity<PostRegisterResponse> create(@RequestBody @Valid PostRegisterRequest request) {
        var response = postApplicationService.createPost(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostRegisterResponse> getById(@PathVariable UUID postId) {
        var postResponse = postApplicationService.getPostById(postId);
        return postResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<PostRegisterResponse>> getAllPosts(Pageable pageable) {
        Page<PostRegisterResponse> posts = postApplicationService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostRegisterResponse> update(@PathVariable UUID postId, @RequestBody @Valid PostRegisterRequest request) {
        var updatedPost = postApplicationService.updatePost(postId, request);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable UUID postId) {
        postApplicationService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
