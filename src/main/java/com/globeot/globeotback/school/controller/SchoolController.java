package com.globeot.globeotback.school.controller;

import com.globeot.globeotback.school.dto.SchoolDetailDto;
import com.globeot.globeotback.school.dto.SchoolListDto;
import com.globeot.globeotback.school.dto.SchoolSearchDto;
import com.globeot.globeotback.school.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schools")
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping("/search")
    public List<SchoolSearchDto> searchSchools(
            @RequestParam(required = false) String name
    ) {
        return schoolService.searchSchools(name);
    }

    @GetMapping
    public List<SchoolListDto> getSchools(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxScore,
            @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            throw new RuntimeException("인증 정보 없음");
        }
        return schoolService.getSchools(keyword, minScore, maxScore);
    }

    @GetMapping("/{schoolId}")
    public SchoolDetailDto getSchoolDetail(
            @PathVariable Long schoolId,
            @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            throw new RuntimeException("인증 정보 없음");
        }
        return schoolService.getSchoolDetail(schoolId, userId);
    }

    @PostMapping("/{schoolId}/favorite")
    public String addFavorite(
            @PathVariable Long schoolId,
            @AuthenticationPrincipal Long userId
    ) {
        return schoolService.addFavorite(userId, schoolId);
    }

    @DeleteMapping("/{schoolId}/favorite")
    public String removeFavorite(
            @PathVariable Long schoolId,
            @AuthenticationPrincipal Long userId
    ) {
        return schoolService.removeFavorite(userId, schoolId);
    }
}