package com.globeot.globeotback.user.dto;

import java.time.LocalDateTime;

public record MyCommentDto(
        Long articleId,
        String title,
        String content,
        LocalDateTime createdAt
) {}