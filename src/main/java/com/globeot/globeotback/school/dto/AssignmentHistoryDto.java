package com.globeot.globeotback.school.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AssignmentHistoryDto {

    private String semester;
    private Double score;
}