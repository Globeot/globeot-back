package com.globeot.globeotback.school.service;

import com.globeot.globeotback.school.domain.School;
import com.globeot.globeotback.school.dto.SchoolDetailDto;
import com.globeot.globeotback.school.dto.SchoolListDto;
import com.globeot.globeotback.school.dto.SchoolSearchDto;
import com.globeot.globeotback.school.repository.FavoriteRepository;
import com.globeot.globeotback.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final FavoriteRepository favoriteRepository;

    public List<SchoolSearchDto> searchSchools(String name) {

        List<School> schools;

        if (name == null || name.trim().isEmpty()) {
            schools = schoolRepository.findAll();
        } else {
            schools = schoolRepository
                    .findTop10ByNameContainingIgnoreCaseOrderByNameAsc(name.trim());
        }

        return schools.stream()
                .map(s -> new SchoolSearchDto(
                        s.getId(),
                        s.getName()
                ))
                .toList();
    }

    public List<SchoolListDto> getSchools(String keyword, Double minScore, Double maxScore) {

        List<School> schools =
                schoolRepository.findByKeywordAndScoreRange(keyword, minScore, maxScore);

        return schools.stream()
                .map(this::toDto)
                .toList();
    }

    private SchoolListDto toDto(School school) {
        return SchoolListDto.builder()
                .schoolId(school.getId())
                .country(school.getCountry())
                .city(school.getCity())
                .schoolName(school.getName())
                .avgScore(school.getAvgScore())
                .travelAccessLevel(school.getTravelAccessLevel())
                .monthlyCost(school.getMonthlyCost())
                .officialSite(school.getOfficialSite())
                .build();
    }

    public SchoolDetailDto getSchoolDetail(Long schoolId, Long userId) {

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("School not found"));

        boolean isFavorite = favoriteRepository
                .existsByUserIdAndSchoolId(userId, schoolId);

        return toDetailDto(school, isFavorite);
    }

    private SchoolDetailDto toDetailDto(School school, boolean isFavorite) {
        return SchoolDetailDto.builder()
                .schoolId(school.getId())
                .imgUrl(school.getImageUrl())
                .name(school.getName())
                .city(school.getCity())
                .country(school.getCountry())
                .popularMajors(school.getPopularMajors())
                .travelAccess(school.getTravelAccess())
                .travelAccessLevel(school.getTravelAccessLevel())
                .monthlyCost(school.getMonthlyCost())
                .monthlyCostLevel(school.getMonthlyCostLevel())
                .internationalStudentRatio(school.getInternationalStudentRatio())
                .buddyProgram(school.getBuddyProgram())
                .officialSite(school.getOfficialSite())
                .isFavorite(isFavorite)
                .build();
    }
}