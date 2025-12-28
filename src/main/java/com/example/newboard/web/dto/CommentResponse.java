package com.example.newboard.web.dto;

import com.example.newboard.domain.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {  // 댓글 조회/응답용 DTO
    private Long id;
    private String authorName; // 닉네임 출력
    private String content;
    private LocalDateTime createdAt;
    private boolean mine;

    // 댓글 등록 직후 응답용
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .mine(true) // 방금 작성한 댓글
                .build();
    }

    // 댓글 조회용
    public static CommentResponse from(Comment comment, Long currentUserId) {
        boolean isMine = currentUserId != null
                && comment.getAuthor().getId().equals(currentUserId);

        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .mine(isMine)
                .build();
    }


}