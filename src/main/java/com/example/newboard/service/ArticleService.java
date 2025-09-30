package com.example.newboard.service;

import com.example.newboard.domain.Article;
import com.example.newboard.repository.ArticleRepository;
import com.example.newboard.repository.UserRepository;
import com.example.newboard.web.dto.ArticleCreateForm;
import com.example.newboard.web.dto.ArticleUpdateForm;
import com.example.newboard.web.dto.ArticleUpdateRequest;
import com.example.newboard.web.dto.ArticleViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // ✅ 게시글 생성 (이미지 업로드 지원)
    @Transactional
    public Long createWithImage(ArticleCreateForm form, String email) {
        var author = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        String imageUrl = null;
        if (form.getFile() != null && !form.getFile().isEmpty()) {
            imageUrl = saveImageFile(form.getFile());
        }

        return articleRepository.save(
                Article.builder()
                        .title(form.getTitle())
                        .content(form.getContent())
                        .category(form.getCategory())
                        .imageUrl(imageUrl)
                        .author(author)
                        .build()
        ).getId();
    }

    // ✅ 게시글 수정 (이미지 업로드 지원)
    @Transactional
    public void updateWithImage(Long id, String email, ArticleUpdateForm form) {
        var article = articleRepository.findByIdAndAuthor_Email(id, email)
                .orElseThrow(() -> new AccessDeniedException("본인 글이 아닙니다."));

        String imageUrl = article.getImageUrl();
        if (form.getFile() != null && !form.getFile().isEmpty()) {
            imageUrl = saveImageFile(form.getFile());
        }

        article.update(form.getTitle(), form.getContent(), form.getCategory(), imageUrl);
    }

    // ✅ 이미지 저장 로직 (공통)
    private String saveImageFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/articles");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path path = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), path);
            return "/uploads/articles/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }

    // ✅ 조회수 증가
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementViewCount(Long id) {
        var article = articleRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found:" + id));
        article.incrementViews();
        articleRepository.saveAndFlush(article);
    }

    // ✅ 게시글 단건 조회 (DTO 변환)
    @Transactional(readOnly = true)
    public ArticleViewDto findByIdForView(Long id) {
        var article = articleRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found:" + id));

        return new ArticleViewDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getCategory(),
                article.getAuthor() != null ? article.getAuthor().getEmail() : "익명",
                article.getCreatedAt(),
                article.getViews(),
                article.getLikeCount(),
                article.getImageUrl()
        );

    }

    // ✅ 편집 페이지에서 사용할 엔티티 그대로 조회
    @Transactional(readOnly = true)
    public Article findById(Long id) {
        return articleRepository.findByIdWithLikes(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found:" + id));
    }

    // ✅ 기존 update (이미지 필요 없는 경우)
    @Transactional
    public void update(Long id, String email, ArticleUpdateRequest req){
        var article = articleRepository.findByIdAndAuthor_Email(id, email)
                .orElseThrow(() -> new AccessDeniedException("본인 글이 아닙니다."));
        article.update(req.getTitle(), req.getContent(), req.getCategory(), article.getImageUrl());
    }

    // ✅ 게시글 삭제
    @Transactional
    public void delete(Long id, String email){
        long result = articleRepository.deleteByIdAndAuthor_Email(id, email);
        if (result == 0)
            throw new AccessDeniedException("본인 글이 아닙니다.");
    }

    // ✅ 카테고리로 게시글 조회
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
                        article.getLikeCount(),
                        article.getImageUrl()
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
                        article.getLikeCount(),
                        article.getImageUrl()
                ))
                .toList();
    }

    // ✅ 좋아요 토글
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
