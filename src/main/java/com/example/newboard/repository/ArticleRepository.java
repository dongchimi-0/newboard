package com.example.newboard.repository;

import com.example.newboard.domain.Article;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    @EntityGraph(attributePaths = "author")
    Optional<Article> findById(Long id);

    @EntityGraph(attributePaths = "author")
    Optional<Article> findByIdAndAuthor_Email(Long id, String email);

    long deleteByIdAndAuthor_Email(Long id, String email);

    List<Article> findByCategory(String category);

    @Query("SELECT a FROM Article a JOIN FETCH a.author WHERE a.category = :category")
    List<Article> findByCategoryWithAuthor(@Param("category") String category);

    @Query("select a FROM Article a JOIN FETCH a.author")
    List<Article> findAllWithAuthor();

}



// CRUD
// findAll()
