package com.example.newboard.web.api;


import com.example.newboard.domain.Article;
import com.example.newboard.repository.UserRepository;
import com.example.newboard.service.ArticleService;
import com.example.newboard.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleApiController {

    private final ArticleService articleService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> create(@Valid @ModelAttribute ArticleCreateForm form, Authentication auth) {
        Long id = articleService.createWithImage(form, auth.getName());
        return ResponseEntity.created(URI.create("/articles/" + id)).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @Valid @ModelAttribute ArticleUpdateForm form,
                                       Authentication auth) {
        articleService.updateWithImage(id, auth.getName(), form);
        return ResponseEntity.noContent().build();
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        articleService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }


    // ✅ 게시글 조회 + 조회수 증가
    @GetMapping("/{id}")
    public ResponseEntity<ArticleViewDto> getArticle(@PathVariable Long id) {
        // 1️⃣ 조회수 증가
        articleService.incrementViewCount(id);

        // 2️⃣ DB에서 최신 데이터 다시 읽어서 DTO 변환
        ArticleViewDto articleDto = articleService.findByIdForView(id);

        // 3️⃣ 클라이언트로 반환
        return ResponseEntity.ok(articleDto);
    }


    // ✅ 좋아요 토글
    @PostMapping("/{id}/like")
    public ResponseEntity<Integer> likeArticle(@PathVariable Long id, Authentication auth) {
        // auth.getName() → 현재 로그인한 사용자의 이메일
        int likeCount = articleService.toggleLike(id, auth.getName());
        return ResponseEntity.ok(likeCount);
    }


}
