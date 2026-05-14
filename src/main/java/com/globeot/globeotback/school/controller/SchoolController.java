package com.globeot.globeotback.school.controller;

import com.globeot.globeotback.global.exception.CustomException;
import com.globeot.globeotback.global.exception.ErrorCode;
import com.globeot.globeotback.global.response.ApiResponse;
import com.globeot.globeotback.school.dto.AssignmentHistoryDto;
import com.globeot.globeotback.school.dto.SchoolArticleListDto;
import com.globeot.globeotback.school.dto.SchoolDetailDto;
import com.globeot.globeotback.school.dto.SchoolListDto;
import com.globeot.globeotback.school.dto.SchoolSearchDto;
import com.globeot.globeotback.school.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schools")
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SchoolSearchDto>>> searchSchools(
            @RequestParam(defaultValue = "") String name
    ) {
        List<SchoolSearchDto> response = schoolService.searchSchools(name);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SchoolListDto>>> getSchools(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore,
            @RequestParam(defaultValue = "false") Boolean noScoreOnly,
            @AuthenticationPrincipal Long userId
    ) {
        validateUserId(userId);
        List<SchoolListDto> response = schoolService.getSchools(keyword, minScore, maxScore, noScoreOnly);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/{schoolId}")
    public ResponseEntity<ApiResponse<SchoolDetailDto>> getSchoolDetail(
            @PathVariable Long schoolId,
            @AuthenticationPrincipal Long userId
    ) {
        validateUserId(userId);
        SchoolDetailDto response = schoolService.getSchoolDetail(schoolId, userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PostMapping("/{schoolId}/favorite")
    public ResponseEntity<ApiResponse<String>> addFavorite(
            @PathVariable Long schoolId,
            @AuthenticationPrincipal Long userId
    ) {
        validateUserId(userId);
        String response = schoolService.addFavorite(userId, schoolId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @DeleteMapping("/{schoolId}/favorite")
    public ResponseEntity<ApiResponse<String>> removeFavorite(
            @PathVariable Long schoolId,
            @AuthenticationPrincipal Long userId
    ) {
        validateUserId(userId);
        String response = schoolService.removeFavorite(userId, schoolId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/{schoolId}/articles")
    public ResponseEntity<ApiResponse<Page<SchoolArticleListDto>>> getSchoolArticles(
            @PathVariable Long schoolId,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Long userId
    ) {
        validateUserId(userId);

        Page<SchoolArticleListDto> response =
                schoolService.getSchoolArticles(schoolId, page);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/{schoolId}/history")
    public ResponseEntity<ApiResponse<Page<AssignmentHistoryDto>>> getSchoolHistory(
            @PathVariable Long schoolId,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Long userId
    ) {
        validateUserId(userId);

        Page<AssignmentHistoryDto> response =
                schoolService.getSchoolHistory(schoolId, page);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
    }
}