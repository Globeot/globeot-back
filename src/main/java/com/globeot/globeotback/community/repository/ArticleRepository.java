package com.globeot.globeotback.community.repository;

import com.globeot.globeotback.community.domain.Article;
import com.globeot.globeotback.community.enums.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findAllByAuthorId(Long userId);

    boolean existsByAuthor_IdAndArticleStatusIn(Long userId, List<ArticleStatus> articleStatuses);

    @Query("""
        SELECT a.id AS articleId,
               a.title AS title,
               a.content AS content,
               a.type AS type,
               a.articleStatus AS articleStatus,
               a.createdAt AS createdAt,
               COUNT(c.id) AS commentCount
        FROM Article a
        LEFT JOIN Comment c ON c.article.id = a.id
        WHERE a.author.id = :userId
        GROUP BY a.id
        ORDER BY a.createdAt DESC
        """)
    List<Object[]> findMyArticlesWithCommentCount(@Param("userId") Long userId);
}