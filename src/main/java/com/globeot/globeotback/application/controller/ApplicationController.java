package com.globeot.globeotback.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.globeot.globeotback.application.dto.ApplicationSubmitDto;
import com.globeot.globeotback.application.dto.MyRankDto;
import com.globeot.globeotback.application.dto.RankingListDto;
import com.globeot.globeotback.application.service.ApplicationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.globeot.globeotback.global.exception.GlobalExceptionHandler.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/application")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String submitApplication(
            @Valid @RequestPart("data") ApplicationSubmitDto request,
            @Parameter(description = "이미지 파일", content = @Content(mediaType = "multipart/form-data"))
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal Long userId
    ) throws BadRequestException, JsonProcessingException {

        if (userId == null) {
            throw new BadRequestException("인증 정보 없음");
        }
        if (image == null || image.isEmpty()) {
            throw new BadRequestException("인증 이미지는 필수입니다.");
        }


        applicationService.createApplication(userId, request, image);

        return "지원서 제출 완료";
    }

    @GetMapping
    public MyRankDto getMyRanking(@AuthenticationPrincipal Long userId) throws Exception {
        if (userId == null) {
            throw new RuntimeException("인증 정보 없음");
        }
        return applicationService.getMyRanking(userId);
    }

    @GetMapping("/rankings")
    public List<RankingListDto> getRankingList(
            @RequestParam(required = false) String schoolName,
            @RequestParam(required = false) String semester
    ) throws Exception {

        return applicationService.getRankingList(schoolName, semester);
    }
}