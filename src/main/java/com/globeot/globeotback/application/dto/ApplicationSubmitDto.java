package com.globeot.globeotback.application.dto;

import com.globeot.globeotback.application.enums.EnglishTestType;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Getter
public class ApplicationSubmitDto {

    @NotNull(message = "TOEFL/IELTS 선택은 필수입니다.")
    private EnglishTestType testType;

    @NotNull(message = "학기는 필수입니다.")
    private String semester;

    @NotEmpty(message = "학교 선택은 필수입니다.")
    private List<SchoolRequest> schools;

    @Getter
    public static class SchoolRequest {

        @NotNull(message = "priority는 필수입니다.")
        private Integer priority;

        private Integer schoolId;

        @NotNull(message = "school_name은 필수입니다.")
        private String schoolName;
    }
}