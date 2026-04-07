package com.globeot.globeotback.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentCreateDto {

    @NotBlank
    @Size(max = 500, message = "댓글은 500자 이내로 입력해주세요.")
    private String content;

    private Long parentId;
}
