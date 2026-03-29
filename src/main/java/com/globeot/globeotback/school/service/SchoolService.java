package com.globeot.globeotback.school.service;

import com.globeot.globeotback.community.repository.ArticleRepository;
import com.globeot.globeotback.school.domain.Favorite;
import com.globeot.globeotback.school.domain.School;
import com.globeot.globeotback.school.dto.SchoolArticleListDto;
import com.globeot.globeotback.school.dto.SchoolDetailDto;
import com.globeot.globeotback.school.dto.SchoolListDto;
import com.globeot.globeotback.school.dto.SchoolSearchDto;
import com.globeot.globeotback.school.repository.FavoriteRepository;
import com.globeot.globeotback.school.repository.SchoolRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

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

    public List<SchoolListDto> getSchools(String keyword, Double minScore, Double maxScore, boolean noScoreOnly) {

        List<School> schools =
                schoolRepository.findByKeywordAndScoreRange(keyword, minScore, maxScore, noScoreOnly);

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
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 학교입니다."));

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

    public String addFavorite(Long userId, Long schoolId) {

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 학교입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        boolean exists = favoriteRepository.existsByUser_IdAndSchool_Id(userId, schoolId);

        if (!exists) {
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .school(school)
                    .createdAt(LocalDateTime.now())
                    .build();

            favoriteRepository.save(favorite);
        }

        return "관심 학교로 등록되었습니다.";
    }

    public String removeFavorite(Long userId, Long schoolId) {

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 학교입니다."));

        favoriteRepository.findByUser_IdAndSchool_Id(userId, schoolId)
                .ifPresent(favoriteRepository::delete);

        return "관심 학교가 해제되었습니다.";
    }

    public List<SchoolArticleListDto> getSchoolArticles(Long schoolId) {
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 학교입니다."));

        return articleRepository.findSchoolArticles(schoolId);
    }

}