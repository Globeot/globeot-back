package com.globeot.globeotback.school.service;

import com.globeot.globeotback.application.repository.AssignmentRepository;
import com.globeot.globeotback.community.repository.ArticleRepository;
import com.globeot.globeotback.global.exception.CustomException;
import com.globeot.globeotback.global.exception.ErrorCode;
import com.globeot.globeotback.school.domain.Favorite;
import com.globeot.globeotback.school.domain.School;
import com.globeot.globeotback.school.dto.AssignmentHistoryDto;
import com.globeot.globeotback.school.dto.SchoolArticleListDto;
import com.globeot.globeotback.school.dto.SchoolDetailDto;
import com.globeot.globeotback.school.dto.SchoolListDto;
import com.globeot.globeotback.school.dto.SchoolScoreDto;
import com.globeot.globeotback.school.dto.SchoolSearchDto;
import com.globeot.globeotback.school.repository.FavoriteRepository;
import com.globeot.globeotback.school.repository.SchoolRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final AssignmentRepository assignmentRepository;

    public List<SchoolSearchDto> searchSchools(String name) {
        List<School> schools;

        if (name == null || name.trim().isEmpty()) {
            schools = schoolRepository.findAll();
        } else {
            schools = schoolRepository.findTop10ByNameContainingIgnoreCaseOrderByNameAsc(name.trim());
        }

        return schools.stream()
                .map(s -> new SchoolSearchDto(
                        s.getId(),
                        s.getName()
                ))
                .toList();
    }

    public List<SchoolListDto> getSchools(String keyword, Double minScore, Double maxScore, boolean noScoreOnly) {
        return schoolRepository.findByKeywordAndScoreRange(keyword, minScore, maxScore, noScoreOnly);
    }

    private SchoolListDto toDto(School school) {
        SchoolScoreDto score = assignmentRepository.findScoreStatsBySchoolId(school.getId());

        return SchoolListDto.builder()
                .schoolId(school.getId())
                .country(school.getCountry())
                .city(school.getCity())
                .schoolName(school.getName())
                .avgScore(score != null ? score.getAvgScore() : null)
                .travelAccessLevel(school.getTravelAccessLevel())
                .monthlyCost(school.getMonthlyCost())
                .officialSite(school.getOfficialSite())
                .build();
    }

    public SchoolDetailDto getSchoolDetail(Long schoolId, Long userId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHOOL_NOT_FOUND));

        boolean isFavorite = favoriteRepository.existsByUserIdAndSchoolId(userId, schoolId);
        SchoolScoreDto score = assignmentRepository.findScoreStatsBySchoolId(schoolId);

        return toDetailDto(school, isFavorite, score);
    }

    private SchoolDetailDto toDetailDto(School school, boolean isFavorite, SchoolScoreDto score) {
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
                .avgScore(score != null ? score.getAvgScore() : null)
                .minScore(score != null ? score.getMinScore() : null)
                .maxScore(score != null ? score.getMaxScore() : null)
                .build();
    }

    public String addFavorite(Long userId, Long schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHOOL_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean exists = favoriteRepository.existsByUser_IdAndSchool_Id(userId, schoolId);

        if (exists) {
            throw new CustomException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .school(school)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favorite);

        return "관심 학교로 등록되었습니다.";
    }

    public String removeFavorite(Long userId, Long schoolId) {
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHOOL_NOT_FOUND));

        Favorite favorite = favoriteRepository.findByUser_IdAndSchool_Id(userId, schoolId)
                .orElseThrow(() -> new CustomException(ErrorCode.FAVORITE_NOT_FOUND));

        favoriteRepository.delete(favorite);

        return "관심 학교가 해제되었습니다.";
    }

    public Page<SchoolArticleListDto> getSchoolArticles(Long schoolId, int page) {
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHOOL_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, 5);

        return articleRepository.findSchoolArticles(schoolId, pageable);
    }

    public Page<AssignmentHistoryDto> getSchoolHistory(Long schoolId, int page) {
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHOOL_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, 5);

        return assignmentRepository.findSchoolHistoryBySchoolId(
                schoolId,
                pageable
        );
    }
}