package com.example.newboard.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Comment {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(columnDefinition = "TEXT", nullable = false)
        private String content;

        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        // 엔티티가 저장될 때 자동으로 시간 넣기
        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now();
        }

        @ManyToOne(fetch = FetchType.LAZY)  // FetchType.LAZY : 지연 로딩
        @JoinColumn(name = "article_id" , nullable = false)  // 댓글은 반드시 게시글에 속해야하니까 not null 제약조건
        private Article article;

        // setter는 최소한으로 (연관관계 편의 메서드에서만 사용)
        // ⚠️ 외부에서 직접 부르면 안 됨 (Article 편의 메서드에서만 사용)
        protected void setArticle(Article article) {
            this.article = article;
        }

        // User 연관관계 (N:1)
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private User author;
}
