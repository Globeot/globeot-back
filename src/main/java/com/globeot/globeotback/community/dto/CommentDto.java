package com.globeot.globeotback.community.dto;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        Long userId,
        String authorNickname,
        String content,
        Long parentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isAuthor
) {}
