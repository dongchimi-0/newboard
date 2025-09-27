package com.example.newboard.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "article")
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
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 필드 이름만 변경 (camelCase)

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

    // ✅ 조회수
    @Column(nullable = false)
    @org.hibernate.annotations.ColumnDefault("0")
    @Builder.Default
    private int views = 0;

    // 조회수 증가
    public void incrementViews() {
        this.views += 1;
    }


    // ✅ 좋아요 누른 사용자
    @ManyToMany
    @JoinTable(
            name = "article_likes",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> likedUsers = new HashSet<>();

    // 좋아요 토글
    public void toggleLike(User user) {
        if (likedUsers.contains(user)) {
            likedUsers.remove(user);
        } else {
            likedUsers.add(user);
        }
    }

    // 좋아요 수 반환
    public int getLikeCount() {
        return likedUsers != null ? likedUsers.size() : 0;
    }


}
