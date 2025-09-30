package com.example.newboard.web.view;

import com.example.newboard.service.ArticleService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {

    private final ArticleService articleService;

    public HomeController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @ModelAttribute
    public void addCsrfToken(CsrfToken token, Model model) {
        model.addAttribute("_csrf", token);
    }

    @GetMapping("/")
    public String home(Model model) {
        // 게시글 데이터만 모델에 추가
        model.addAttribute("noticePosts", articleService.findByCategory("공지"));
        model.addAttribute("freePosts", articleService.findByCategory("자유"));
        return "home";
    }
}
