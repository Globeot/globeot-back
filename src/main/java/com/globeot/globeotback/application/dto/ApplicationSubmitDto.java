package com.globeot.globeotback.application.dto;

import lombok.Getter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Getter
public class ApplicationSubmitDto {

    @NotNull(message = "환산 점수는 필수입니다.")
    private Double convertedScore;

    @NotNull(message = "학기는 필수입니다.")
    private String semester;

    @NotEmpty(message = "학교 선택은 필수입니다.")
    private List<SchoolRequest> schools;

    @Getter
    public static class SchoolRequest {

        @NotNull(message = "priority는 필수입니다.")
        private Integer priority;

        @NotNull(message = "school_id는 필수입니다.")
        private Long school_id;
    }
}