package com.globeot.globeotback.school.controller;

import com.globeot.globeotback.school.dto.SchoolListDto;
import com.globeot.globeotback.school.dto.SchoolSearchDto;
import com.globeot.globeotback.school.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) Double maxScore
    ) {
        return schoolService.getSchools(keyword, minScore, maxScore);
    }
}