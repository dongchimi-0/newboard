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

    // 엔티티 -> DTO 변환 메서드
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName()) // User 엔티티의 닉네임
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}