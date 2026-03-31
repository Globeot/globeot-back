package com.globeot.globeotback.school.dto;

import com.globeot.globeotback.school.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SchoolDetailDto {

    private Long schoolId;
    private String imgUrl;
    private String name;
    private String city;
    private String country;
    private List<String> popularMajors;

    private String travelAccess;
    private Level travelAccessLevel;

    private String monthlyCost;
    private Level monthlyCostLevel;

    private int internationalStudentRatio;
    private String buddyProgram;
    private String officialSite;

    private boolean isFavorite;
}