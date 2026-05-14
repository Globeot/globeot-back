package com.globeot.globeotback.community.repository;

import com.globeot.globeotback.community.domain.Article;
import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.Region;
import com.globeot.globeotback.community.enums.Type;
import com.globeot.globeotback.school.dto.SchoolArticleListDto;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
    SELECT new com.globeot.globeotback.school.dto.SchoolArticleListDto(
        a.id,
        a.school.id,
        CAST(a.exchangeStatus AS string),
        a.title,
        a.createdAt,
        u.nickname,
        COUNT(c.id)
    )
    FROM Article a
    JOIN a.author u
    LEFT JOIN Comment c ON c.article.id = a.id
    WHERE a.school.id = :schoolId
    GROUP BY a.id, a.exchangeStatus, a.title, a.createdAt, u.nickname
    ORDER BY a.createdAt DESC
""",
            countQuery = """
    SELECT COUNT(DISTINCT a.id)
    FROM Article a
    WHERE a.school.id = :schoolId
""")
    Page<SchoolArticleListDto> findSchoolArticles(
            @Param("schoolId") Long schoolId,
            Pageable pageable
    );

    @Query(value = """
        SELECT a, COUNT(c.id)
        FROM Article a
        JOIN a.author u
        LEFT JOIN Comment c ON c.article.id = a.id
        WHERE (:keyword IS NULL OR
               REPLACE(a.title, ' ', '') LIKE CONCAT('%', :keyword, '%') OR
               REPLACE(a.content, ' ', '') LIKE CONCAT('%', :keyword, '%'))
        AND (:exchangeStatus IS NULL OR a.exchangeStatus = :exchangeStatus)
        AND (:region IS NULL OR a.region = :region)
        AND (:type IS NULL OR a.type = :type)
        AND (:topic IS NULL OR a.topic = :topic)
        AND (a.reportCount IS NULL OR a.reportCount < 5)
        GROUP BY a.id
        ORDER BY a.createdAt DESC
        """,
        countQuery = """
        SELECT COUNT(DISTINCT a.id)
        FROM Article a
        JOIN a.author u
        WHERE (:keyword IS NULL OR
               REPLACE(a.title, ' ', '') LIKE CONCAT('%', :keyword, '%') OR
               REPLACE(a.content, ' ', '') LIKE CONCAT('%', :keyword, '%'))
        AND (:exchangeStatus IS NULL OR a.exchangeStatus = :exchangeStatus)
        AND (:region IS NULL OR a.region = :region)
        AND (:type IS NULL OR a.type = :type)
        AND (:topic IS NULL OR a.topic = :topic)
        AND (a.reportCount IS NULL OR a.reportCount < 5)
        """)
    Page<Object[]> findArticlesWithFilter(
            @Param("keyword") String keyword,
            @Param("exchangeStatus") ExchangeStatus exchangeStatus,
            @Param("region") Region region,
            @Param("type") Type type,
            @Param("topic") String topic,
            Pageable pageable
    );
}
