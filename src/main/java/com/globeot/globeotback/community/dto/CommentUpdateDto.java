package com.globeot.globeotback.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentUpdateDto {
    @NotBlank
    private String content;
}
