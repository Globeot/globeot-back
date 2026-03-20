package com.globeot.globeotback.community.repository;

import com.globeot.globeotback.community.domain.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    @Query("""
        SELECT s.id, a.id, a.title, a.content, a.type, a.articleStatus, a.createdAt
        FROM Scrap s
        JOIN s.article a
        WHERE s.user.id = :userId
        ORDER BY s.createdAt DESC
        """)
    List<Object[]> findMyScrapsWithArticle(@Param("userId") Long userId);
}