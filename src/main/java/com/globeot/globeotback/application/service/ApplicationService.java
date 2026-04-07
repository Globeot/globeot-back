package com.globeot.globeotback.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globeot.globeotback.application.domain.Application;
import com.globeot.globeotback.application.dto.ApplicationSubmitDto;
import com.globeot.globeotback.application.dto.MyRankDto;
import com.globeot.globeotback.application.dto.RankingListDto;
import com.globeot.globeotback.application.enums.Status;
import com.globeot.globeotback.application.repository.ApplicationRepository;
import com.globeot.globeotback.global.exception.CustomException;
import com.globeot.globeotback.global.exception.ErrorCode;
import com.globeot.globeotback.school.repository.SchoolRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final SchoolRepository schoolRepository;
    private final ObjectMapper objectMapper;

    private void validateSchools(List<ApplicationSubmitDto.SchoolRequest> schools) {
        if (schools == null || schools.isEmpty()) {
            throw new CustomException(ErrorCode.APPLICATION_SCHOOL_REQUIRED);
        }

        boolean hasPriority1 = schools.stream()
                .anyMatch(s -> s.getPriority() == 1);

        if (!hasPriority1) {
            throw new CustomException(ErrorCode.APPLICATION_PRIORITY1_REQUIRED);
        }
    }

    public void createApplication(
            Long userId,
            ApplicationSubmitDto request,
            MultipartFile gpaImage,
            MultipartFile englishScoreImage
    ) throws JsonProcessingException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (applicationRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.APPLICATION_ALREADY_SUBMITTED);
        }

        validateSchools(request.getSchools());

        String gpaImageUrl = s3Service.upload(gpaImage);
        String englishScoreImageUrl = s3Service.upload(englishScoreImage);

        List<Map<String, Object>> schoolsJsonList = request.getSchools().stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("school_id", s.getSchoolId());
                    map.put("school_name", s.getSchoolName());
                    map.put("priority", s.getPriority());
                    return map;
                })
                .collect(Collectors.toList());

        String schoolsJson = objectMapper.writeValueAsString(schoolsJsonList);

        Application application = Application.builder()
                .user(user)
                .englishTestType(request.getTestType())
                .gpa(null)
                .convertedScore(null)
                .englishScore(null)
                .gpaImageUrl(gpaImageUrl)
                .englishTestImageUrl(englishScoreImageUrl)
                .semester(request.getSemester())
                .schools(schoolsJson)
                .status(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        applicationRepository.save(application);
    }

    public MyRankDto getMyRanking(Long userId) throws Exception {

        Application myApplication = applicationRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        if (myApplication.getStatus() == Status.PENDING) {
            throw new CustomException(ErrorCode.APPLICATION_PENDING);
        }

        List<Application> approvedApplications =
                applicationRepository.findByStatusOrderByConvertedScoreDesc(Status.APPROVED);

        int totalApplicants = approvedApplications.size();

        int myRank = approvedApplications.indexOf(myApplication) + 1;

        List<Map<String, Object>> schoolsList = objectMapper.readValue(
                myApplication.getSchools(),
                new TypeReference<List<Map<String, Object>>>() {}
        );

        List<MyRankDto.SchoolRanking> schoolRankings = schoolsList.stream()
                .map(s -> {
                    Integer priority = (Integer) s.get("priority");
                    Long schoolId = s.get("school_id") != null
                            ? Long.valueOf(s.get("school_id").toString())
                            : null;

                    String schoolName = schoolId != null
                            ? schoolRepository.findById(schoolId)
                            .map(school -> school.getName())
                            .orElse((String) s.get("school_name"))
                            : (String) s.get("school_name");

                    return new MyRankDto.SchoolRanking(
                            schoolId != null ? Math.toIntExact(schoolId) : null,
                            schoolName,
                            priority
                    );
                })
                .sorted(Comparator.comparingInt(MyRankDto.SchoolRanking::getPriority))
                .toList();

        return new MyRankDto(
                myRank,
                totalApplicants,
                myApplication.getConvertedScore(),
                schoolRankings
        );
    }

    public List<RankingListDto> getRankingList(String schoolName, String semester, Long userId) throws Exception {
        List<Application> applications =
                applicationRepository.findByStatusOrderByConvertedScoreDesc(Status.APPROVED);

        List<RankingListDto> tempList = new ArrayList<>();

        for (Application app : applications) {
            List<Map<String, Object>> schools =
                    objectMapper.readValue(app.getSchools(), new TypeReference<List<Map<String, Object>>>() {});

            List<RankingListDto.SchoolInfo> schoolInfos =
                    schools.stream()
                            .map(s -> new RankingListDto.SchoolInfo(
                                    (String) s.get("school_name"),
                                    s.get("school_id") != null
                                            ? Long.valueOf(s.get("school_id").toString())
                                            : null,
                                    (Integer) s.get("priority")
                            ))
                            .toList();

            boolean isMine = app.getUser().getId().equals(userId);

            tempList.add(new RankingListDto(
                    0,
                    app.getConvertedScore(),
                    app.getGpa(),
                    app.getEnglishTestType().name(),
                    app.getSemester(),
                    schoolInfos,
                    isMine
            ));
        }

        List<RankingListDto> filtered = tempList.stream()
                .filter(dto -> {
                    boolean matchSchool = true;
                    boolean matchSemester = true;

                    if (schoolName != null && !schoolName.trim().isEmpty()) {
                        matchSchool = dto.getSchools().stream()
                                .anyMatch(s -> s.getSchoolName() != null &&
                                        s.getSchoolName().toLowerCase().contains(schoolName.toLowerCase().trim()));
                    }

                    if (semester != null && !semester.trim().isEmpty()) {
                        matchSemester = dto.getSemester() != null &&
                                dto.getSemester().equals(semester.trim());
                    }

                    return matchSchool && matchSemester;
                })
                .toList();

        List<RankingListDto> result = new ArrayList<>();
        int rank = 0;
        Double prevScore = null;

        for (int i = 0; i < filtered.size(); i++) {
            RankingListDto dto = filtered.get(i);
            Double currentScore = dto.getScore();

            if (prevScore == null || !Objects.equals(prevScore, currentScore)) {
                rank = i + 1;
            }
            prevScore = currentScore;

            result.add(new RankingListDto(
                    rank,
                    dto.getScore(),
                    dto.getGpa(),
                    dto.getTestType(),
                    dto.getSemester(),
                    dto.getSchools(),
                    dto.isMine()
            ));
        }

        return result;
    }
}