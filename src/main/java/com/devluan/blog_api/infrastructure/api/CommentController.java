package com.devluan.blog_api.infrastructure.api;

import com.devluan.blog_api.application.dto.comment.request.CommentRegisterRequest;
import com.devluan.blog_api.application.dto.comment.response.CommentRegisterResponse;
import com.devluan.blog_api.application.service.comment.CommentApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentApplicationService commentApplicationService;

    @PostMapping("/new")
    public ResponseEntity<CommentRegisterResponse> create(@RequestBody @Valid CommentRegisterRequest request) {
        var response = commentApplicationService.createComment(request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentRegisterResponse> update(@PathVariable UUID commentId, @RequestBody @Valid CommentRegisterRequest request) {
        var updatedComment = commentApplicationService.updateComment(commentId, request);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable UUID commentId) {
        commentApplicationService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
