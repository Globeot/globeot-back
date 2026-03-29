package com.globeot.globeotback.school.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SchoolArticleListDto {

    private Long articleId;
    private Long schoolId;
    private String exchangeStatus;
    private String title;
    private LocalDateTime createdAt;
    private String nickname;
    private Long commentCount;
}