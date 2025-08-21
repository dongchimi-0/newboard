package com.example.newboard.service;

import com.example.newboard.domain.Article;
import com.example.newboard.repository.ArticleRepository;
import com.example.newboard.repository.UserRepository;
import com.example.newboard.web.dto.ArticleCreateRequest;
import com.example.newboard.web.dto.ArticleUpdateRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.example.newboard.web.dto.ArticleViewDto;
import java.time.format.DateTimeFormatter;


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

    public Article findById(Long id) {
        return articleRepository.findById(id)
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

        if (articleRepository.deleteByIdAndAuthor_Email(id, email) == 0)
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
                        article.getCategory(),
                        article.getAuthor() != null ? article.getAuthor().getEmail() : "익명",
                        article.getCreatedAt()
                ))
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleViewDto> findAllForView() {
        return articleRepository.findAllWithAuthor().stream()
                .map(article -> new ArticleViewDto(
                        article.getId(),
                        article.getTitle(),
                        article.getCategory(),
                        article.getAuthor() != null ? article.getAuthor().getEmail() : "익명",
                        article.getCreatedAt()
                ))
                .toList();
    }



}
