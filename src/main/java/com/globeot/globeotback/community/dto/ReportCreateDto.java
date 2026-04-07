package com.globeot.globeotback.community.dto;

import com.globeot.globeotback.community.enums.Reason;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReportCreateDto {
    @NotNull
    private Reason reason;
}
