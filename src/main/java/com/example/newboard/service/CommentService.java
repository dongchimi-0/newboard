package com.example.newboard.service;

import com.example.newboard.domain.Article;
import com.example.newboard.domain.Comment;
import com.example.newboard.domain.User;
import com.example.newboard.repository.ArticleRepository;
import com.example.newboard.repository.CommentRepository;
import com.example.newboard.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 등록
    public Comment addComment(Long articleId, String content) {
        // 로그인한 사용자 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .content(content)
                .author(user)   // ✅ User 객체로 저장
                .article(article)
                .build();

        return commentRepository.save(comment);
    }

    // 댓글 삭제 (본인만 삭제 가능)
    public void removeComment(Long articleId, Long commentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getArticle().getId().equals(articleId)) {
            throw new IllegalArgumentException("이 댓글은 해당 게시글에 속하지 않습니다.");
        }

        // 관리자 또는 본인만 삭제 가능
        if (!isAdmin && !comment.getAuthor().getEmail().equals(email)) {
            throw new SecurityException("본인 혹은 관리자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }


    // 댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> getComments(Long articleId) {
        // 바로 repository에서 가져옴
        return commentRepository.findByArticleId(articleId);
    }
}