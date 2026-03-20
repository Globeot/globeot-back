package com.globeot.globeotback.user.dto;

import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.Type;

import java.time.LocalDateTime;

public record MyArticleDto(
        Long articleId,
        String title,
        String content,
        Type type,
        ArticleStatus articleStatus,
        LocalDateTime createdAt,
        Long commentCount
) {}