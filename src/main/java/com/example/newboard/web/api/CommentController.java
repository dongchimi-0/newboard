package com.example.newboard.web.api;

import com.example.newboard.domain.Comment;
import com.example.newboard.service.CommentService;
import com.example.newboard.web.dto.CommentRequest;
import com.example.newboard.web.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ✅ 댓글 등록
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long articleId,
            @RequestBody CommentRequest request  // ✅ 요청용 DTO로 교체
    ) {
        Comment savedComment = commentService.addComment(articleId, request.getContent());
        return ResponseEntity.ok(CommentResponse.from(savedComment));
    }

    // ✅ 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long articleId,
            @PathVariable Long commentId
    ) {
        commentService.removeComment(articleId, commentId);
        return ResponseEntity.noContent().build();
    }

    // ✅ 댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long articleId) {
        List<Comment> comments = commentService.getComments(articleId);
        List<CommentResponse> response = comments.stream()
                .map(CommentResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }
}
