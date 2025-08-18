package com.example.newboard.web.view;

import com.example.newboard.service.ArticleService;
import com.example.newboard.web.dto.ArticleCreateRequest;
import com.example.newboard.web.dto.ArticleUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ArticleViewController {
    private final ArticleService articleService;

    @GetMapping("/articles")
    public String list(Model model){
        model.addAttribute("articles", articleService.findAll());
        return "article-list";
    }

    @GetMapping("/articles/new")
    public String createForm() { return "article-form";}

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
}
