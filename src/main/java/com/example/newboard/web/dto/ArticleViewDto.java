package com.example.newboard.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleViewDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String authorEmail;
    private LocalDateTime createdAt;
    private int views;
    private int likeCount;
}
