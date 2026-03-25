package com.globeot.globeotback.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RankingListDto {

    private int rank;
    private Double score;
    private String testType;
    private String semester;
    private List<SchoolInfo> schools;

    @Getter
    @AllArgsConstructor
    public static class SchoolInfo {
        private String schoolName;
        private Long schoolId;
        private Integer priority;
    }
}