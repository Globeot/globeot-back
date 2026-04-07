package com.globeot.globeotback.application.controller;

import com.globeot.globeotback.application.dto.ApplicationSubmitDto;
import com.globeot.globeotback.application.dto.MyRankDto;
import com.globeot.globeotback.application.dto.RankingListDto;
import com.globeot.globeotback.application.service.ApplicationService;
import com.globeot.globeotback.global.exception.CustomException;
import com.globeot.globeotback.global.exception.ErrorCode;
import com.globeot.globeotback.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<String>> submitApplication(
            @Valid @RequestPart("data") ApplicationSubmitDto request,
            @RequestPart("gpaImage") MultipartFile gpaImage,
            @RequestPart("englishScoreImage") MultipartFile englishImage,
            @AuthenticationPrincipal Long userId
    ) throws Exception {

        validateUserId(userId);

        if (gpaImage == null || gpaImage.isEmpty()) {
            throw new CustomException(ErrorCode.GPA_IMAGE_REQUIRED);
        }

        if (englishImage == null || englishImage.isEmpty()) {
            throw new CustomException(ErrorCode.ENGLISH_SCORE_IMAGE_REQUIRED);
        }

        applicationService.createApplication(userId, request, gpaImage, englishImage);

        return ResponseEntity.ok(ApiResponse.onSuccess("지원서 제출이 완료되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MyRankDto>> getMyRanking(@AuthenticationPrincipal Long userId) throws Exception {
        validateUserId(userId);
        MyRankDto response = applicationService.getMyRanking(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/rankings")
    public ResponseEntity<ApiResponse<List<RankingListDto>>> getRankingList(
            @RequestParam(required = false) String schoolName,
            @RequestParam(required = false) String semester,
            @AuthenticationPrincipal Long userId
    ) throws Exception {
        validateUserId(userId);
        List<RankingListDto> response = applicationService.getRankingList(schoolName, semester, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
    }
}