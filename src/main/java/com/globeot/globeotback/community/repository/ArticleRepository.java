package com.globeot.globeotback.community.repository;

import com.globeot.globeotback.community.domain.Article;
import com.globeot.globeotback.community.enums.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findAllByAuthorId(Long userId);

    boolean existsByAuthor_IdAndArticleStatusIn(Long userId, List<ArticleStatus> articleStatuses);
}