package com.example.newboard.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

//    @CreatedDate
//    @Column(updatable = false)
//    private LocalDateTime createdDate;

//    public LocalDateTime getCreatedDate() {
//        return createdDate;
//    }
//
//    @PrePersist
//    public void prePersist(){
//        this.createdDate = LocalDateTime.now();
//    }

//    public LocalDateTime getCreatedDate(){
//        return createdDate;
//    }

    // ✅ 작성일 추가
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 엔티티가 저장될 때 자동으로 시간 넣기
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}
