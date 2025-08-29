package com.example.newboard.repository;

import com.example.newboard.domain.Comment;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    @EntityGraph(attributePaths = {"author"})
    List<Comment> findByArticleId(Long articleId);
}
