package com.devluan.blog_api.infrastructure.api;

import com.devluan.blog_api.application.dto.comment.request.CommentRegisterRequest;
import com.devluan.blog_api.application.dto.comment.response.CommentRegisterResponse;
import com.devluan.blog_api.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/new")
    public ResponseEntity<CommentRegisterResponse> create(@RequestBody @Valid CommentRegisterRequest request) {
        var response = commentService.createComment(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentRegisterResponse> update(@PathVariable UUID commentId, @RequestBody @Valid CommentRegisterRequest request) {
        var updatedComment = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
