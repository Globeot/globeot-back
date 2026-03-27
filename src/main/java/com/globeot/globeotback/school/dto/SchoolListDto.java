package com.globeot.globeotback.school.dto;
import com.globeot.globeotback.school.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SchoolListDto {

    private Long schoolId;
    private String country;
    private String city;
    private String schoolName;
    private Double avgScore;
    private Level travelAccessLevel;
    private String monthlyCost;
    private String officialSite;
}