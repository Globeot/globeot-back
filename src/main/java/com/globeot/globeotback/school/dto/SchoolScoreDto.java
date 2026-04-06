package com.globeot.globeotback.school.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SchoolScoreDto {
    private Double avgScore;
    private Double minScore;
    private Double maxScore;
}