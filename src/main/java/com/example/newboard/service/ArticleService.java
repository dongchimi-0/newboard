package com.example.newboard.service;

import com.example.newboard.domain.Article;
import com.example.newboard.repository.ArticleRepository;
import com.example.newboard.repository.UserRepository;
import com.example.newboard.web.dto.ArticleCreateRequest;
import com.example.newboard.web.dto.ArticleUpdateRequest;
import com.example.newboard.web.dto.ArticleViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    @Transactional
    public Long create(ArticleCreateRequest req, String email){
        var author = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        return articleRepository.save(
                Article.builder()
                        .title(req.getTitle())
                        .content(req.getContent())
                        .category(req.getCategory())
                        .author(author)
                        .build()
        ).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementViewCount(Long id) {
        var article = articleRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found:" + id));
        article.incrementViews();
        articleRepository.saveAndFlush(article); // DB에 즉시 반영 + 영속성 컨텍스트 flush
    }


    @Transactional(readOnly = true)
    public ArticleViewDto findByIdForView(Long id) {
        var article = articleRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found:" + id));

        System.out.println("[DEBUG] 조회수 값 확인: " + article.getViews());

        return new ArticleViewDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getCategory(),
                article.getAuthor() != null ? article.getAuthor().getEmail() : "익명",
                article.getCreatedAt(),
                article.getViews(),
                article.getLikeCount()
        );
    }


    // ✅ 편집 페이지 전용 (엔티티 그대로 반환)
    @Transactional(readOnly = true)
    public Article findById(Long id) {
        return articleRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found:" + id));
    }

    @Transactional
    public void update(Long id, String email, ArticleUpdateRequest req){
        var article = articleRepository.findByIdAndAuthor_Email(id, email)
                .orElseThrow(() -> new AccessDeniedException("본인 글이 아닙니다."));
        article.update(req.getTitle(), req.getContent(), req.getCategory());
    }

    @Transactional
    public void delete(Long id, String email){
        long result = articleRepository.deleteByIdAndAuthor_Email(id, email);
        System.out.println("[DEBUG] 삭제 결과: " + result);
        if (result == 0)
            throw new AccessDeniedException("본인 글이 아닙니다.");
    }

    public List<Article> findByCategory(String category) {
        return articleRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public List<ArticleViewDto> findByCategoryForView(String category) {
        return articleRepository.findByCategoryWithAuthor(category).stream()
                .map(article -> new ArticleViewDto(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getCategory(),
                        article.getAuthor() != null ? article.getAuthor().getEmail() : "익명",
                        article.getCreatedAt(),
                        article.getViews(),
                        article.getLikeCount()
                ))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleViewDto> findAllForView() {
        return articleRepository.findAllWithAuthor().stream()
                .map(article -> new ArticleViewDto(
                        article.getId(),
                        article.getTitle(),
                        article.getContent(),
                        article.getCategory(),
                        article.getAuthor() != null ? article.getAuthor().getEmail() : "익명",
                        article.getCreatedAt(),
                        article.getViews(),
                        article.getLikeCount()
                ))
                .toList();
    }

    @Transactional
    public Article incrementView(Long articleId) {
        Article article = articleRepository.findByIdWithLikes(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found:" + articleId));
        article.incrementViews();
        return article;
    }

    @Transactional
    public int toggleLike(Long articleId, String userEmail) {
        Article article = articleRepository.findByIdWithLikes(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found:" + articleId));

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found:" + userEmail));

        article.toggleLike(user);
        return article.getLikeCount();
    }
}
