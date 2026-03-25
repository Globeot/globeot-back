package com.globeot.globeotback.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class MyRankDto {
    private int myRank;
    private int totalApplicants;
    private double myScore;
    private List<SchoolRanking> mySchoolRankings;

    @Data
    @AllArgsConstructor
    public static class SchoolRanking {
        private String schoolName;
        private int priority;
    }
}