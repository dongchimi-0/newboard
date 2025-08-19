package com.example.newboard.web.view;

import com.example.newboard.domain.Article;
import com.example.newboard.service.ArticleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    public String home(Model model, @AuthenticationPrincipal UserDetails user) {
        List<String> profileImages = Arrays.asList(
                "profiles/바트.jpg",
                "profiles/보노보노.jpg",
                "profiles/빵빵이.jpg",
                "profiles/소클라임.jpg",
                "profiles/슈가슈가룬.jpg",
                "profiles/스쿠나.jpg",
                "profiles/심슨.png",
                "profiles/원피스1.jpg",
                "profiles/키티.png",
                "profiles/품쿠린.jpg"
        );

        Random random = new Random();
        String randomProfileImage = profileImages.get(random.nextInt(profileImages.size()));

        model.addAttribute("profileImage", randomProfileImage);

        if (user != null) {
            model.addAttribute("displayName", user.getUsername());
        }

        // 공지사항 게시글 리스트 가져오기 (예: category = "notice")
        List<Article> noticePosts = articleService.findByCategory("notice");
        // 자유게시판 게시글 리스트 가져오기 (예: category = "free")
        List<Article> freePosts = articleService.findByCategory("free");

        model.addAttribute("noticePosts", noticePosts);
        model.addAttribute("freePosts", freePosts);

        return "home";
    }
}
