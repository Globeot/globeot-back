package com.globeot.globeotback.community.repository;

import com.globeot.globeotback.community.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUserId(Long userId);

    @Query("""
        SELECT c.article.id, c.article.title, c.content, c.createdAt
        FROM Comment c
        WHERE c.user.id = :userId
        ORDER BY c.createdAt DESC
        """)
    List<Object[]> findMyCommentsWithArticleTitle(@Param("userId") Long userId);
    Long countByArticle_Id(Long articleId);
}