package com.example.newboard.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 50)
    private String category;

    public void update(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;  // User 테이블의 id 와 연결되는 외래키 컬럼


    // ✅ 작성일 추가
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 엔티티가 저장될 때 자동으로 시간 넣기
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 게시글 ↔ 댓글 : 1:N 관계 (양방향)
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default   // 빌더 사용시에도 초기화 보장
    private List<Comment> comments = new ArrayList<>();

    // 양방향 연관관계 편의 메서드(댓글 추가 편의 메서드)
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setArticle(this);  // 양방향 일관성 유지
    }

    // 댓글 삭제 편의 메서드
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setArticle(null);
    }

}
