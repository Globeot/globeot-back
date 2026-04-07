package com.globeot.globeotback.community.dto;

import java.util.List;

public record ArticlePageDto(
        List<ArticleListDto> content,
        int totalPages,
        long totalElements
) {}
