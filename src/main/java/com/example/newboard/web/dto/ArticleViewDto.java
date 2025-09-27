package com.example.newboard.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleViewDto {
    private Long id;
    private String title;
    private String category;
    private String authorEmail;
    private LocalDateTime createdAt; // 포맷이 필요하다면 String으로
    private int views;
    private int likeCount;
}
