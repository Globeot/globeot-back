package com.globeot.globeotback.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.globeot.globeotback.application.dto.ApplicationSubmitDto;
import com.globeot.globeotback.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/application")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public String submitApplication(
            @Valid @RequestPart("data") ApplicationSubmitDto request,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal Long userId
    ) throws BadRequestException, JsonProcessingException {

        if (userId == null) {
            throw new RuntimeException("인증 정보 없음");
        }
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("인증 이미지는 필수입니다.");
        }


        applicationService.createApplication(userId, request, image);

        return "지원서 제출 완료";
    }
}