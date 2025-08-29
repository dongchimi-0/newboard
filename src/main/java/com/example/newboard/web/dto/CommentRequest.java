package com.example.newboard.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentRequest {  // 댓글 등록 시 요청용 DTO
    private String content;
}