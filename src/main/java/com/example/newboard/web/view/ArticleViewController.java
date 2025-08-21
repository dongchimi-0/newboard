package com.example.newboard.web.view;

import com.example.newboard.domain.Article;
import com.example.newboard.service.ArticleService;
import com.example.newboard.web.dto.ArticleCreateRequest;
import com.example.newboard.web.dto.ArticleUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ArticleViewController {
    private final ArticleService articleService;

    @GetMapping("/articles")
    public String list(Model model) {
        model.addAttribute("articles", articleService.findAllForView());
        return "article-list";  // html 이동
    }


    @GetMapping("/articles/new")
    public String createForm(Model model) {
        model.addAttribute("articleCreateRequest", new ArticleCreateRequest());
        return "article-form";
    }

    @GetMapping("/articles/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth){
        var article = articleService.findById(id);
        model.addAttribute("article", article);
        boolean isOwner = auth != null && article.getAuthor().getEmail().equals(auth.getName());
        model.addAttribute("isOwner", isOwner);
        return "article-detail";
    }

    @GetMapping("/articles/{id}/edit")
    public String editForm(@PathVariable Long id, Model model){
        var article = articleService.findById(id);
        model.addAttribute("article", article);
        return "article-edit";
    }

    // 공지 카테고리 페이지
    @GetMapping("/articles/category/notice")
    public String noticeCategory(Model model) {
        List<Article> articles = articleService.findByCategory("공지");
        model.addAttribute("articles", articles);
        return "category-notice";
    }

    // 자유 카테고리 페이지
    @GetMapping("/articles/category/free")
    public String freeCategory(Model model) {
        List<Article> articles = articleService.findByCategory("자유");
        model.addAttribute("articles", articles);
        return "category-free";
    }

    // 질문 카테고리 페이지
    @GetMapping("/articles/category/qna")
    public String questionCategory(Model model) {
        List<Article> articles = articleService.findByCategory("질문");
        model.addAttribute("articles", articles);
        return "category-qna";
    }

    // 일기 카테고리 페이지
    @GetMapping("/articles/category/diary")
    public String diaryCategory(Model model) {
        List<Article> articles = articleService.findByCategory("일기");
        model.addAttribute("articles", articles);
        return "category-diary";
    }

//    @GetMapping("/articles/category/{category}")
//    public String getCategoryPage(@PathVariable String category, Model model) {
//        // 모든 카테고리 페이지에서 필요한 공통 로직
//        List<Article> articles = articleService.findByCategory(category);
//        model.addAttribute("articles", articles);
//
//        // 카테고리 값에 따라 다른 HTML 파일 반환
//        if ("notice".equals(category)) {
//            return "category-notice";
//        } else if ("free".equals(category)) {
//            return "category-free";
//        } else if ("qna".equals(category)) {
//            return "category-qna";
//        } else if ("diary".equals(category)) {
//            return "category-diary";
//        } else {
//            // 존재하지 않는 카테고리일 경우 에러 페이지 또는 기본 페이지로 리다이렉트
//            return "redirect:/articles";
//        }
//    }


}
