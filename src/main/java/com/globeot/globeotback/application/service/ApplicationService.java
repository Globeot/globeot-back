package com.globeot.globeotback.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globeot.globeotback.application.domain.Application;
import com.globeot.globeotback.application.dto.ApplicationSubmitDto;
import com.globeot.globeotback.application.dto.MyRankDto;
import com.globeot.globeotback.application.enums.Status;
import com.globeot.globeotback.application.repository.ApplicationRepository;
import com.globeot.globeotback.school.repository.SchoolRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;
import com.globeot.globeotback.global.exception.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final SchoolRepository schoolRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 한 번만 생성

    private void validateSchools(List<ApplicationSubmitDto.SchoolRequest> schools) {
        if (schools == null || schools.isEmpty()) {
            throw new GlobalExceptionHandler.BadRequestException("학교 최소 1개 선택은 필수입니다.");
        }

        boolean hasPriority1 = schools.stream()
                .anyMatch(s -> s.getPriority() == 1);

        if (!hasPriority1) {
            throw new GlobalExceptionHandler.BadRequestException("1순위 학교 선택은 필수입니다.");
        }
    }

    public void createApplication(Long userId, ApplicationSubmitDto request, MultipartFile image)
            throws JsonProcessingException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (applicationRepository.existsByUserId(userId)) {
            throw new GlobalExceptionHandler.BadRequestException("이미 지원 내역을 제출한 사용자입니다.");
        }

        validateSchools(request.getSchools());

        String imageUrl = s3Service.upload(image);

        // schools JSON 변환
        List<Map<String, Object>> schoolsJsonList = request.getSchools().stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("school_name", s.getSchoolName()); // 이름은 항상 넣음
                    map.put("priority", s.getPriority());
                    return map;
                })
                .collect(Collectors.toList());

        String schoolsJson = objectMapper.writeValueAsString(schoolsJsonList);

        Application application = Application.builder()
                .user(user)
                .convertedScore(request.getConvertedScore())
                .certificateImageUrl(imageUrl)
                .semester(request.getSemester())
                .schools(schoolsJson)
                .status(Status.SUBMITTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        applicationRepository.save(application);
    }

    public MyRankDto getMyRanking(Long userId) throws Exception {
        List<Application> allApplications = applicationRepository.findAllByOrderByConvertedScoreDesc();

        int totalApplicants = allApplications.size();

        Application myApplication = allApplications.stream()
                .filter(app -> app.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("신청 정보 없음"));

        int myRank = allApplications.indexOf(myApplication) + 1;

        // schools JSON 문자열 파싱
        List<Map<String, Object>> schoolsList = objectMapper.readValue(
                myApplication.getSchools(),
                new TypeReference<>() {}
        );

        List<MyRankDto.SchoolRanking> schoolRankings = schoolsList.stream()
                .map(s -> {
                    Integer priority = (Integer) s.get("priority");
                    Long schoolId = s.get("school_id") != null ? Long.valueOf((Integer)s.get("school_id")) : null;
                    String schoolName = schoolId != null
                            ? schoolRepository.findById(schoolId).map(school -> school.getName()).orElse("Unknown")
                            : (String) s.get("school_name");
                    return new MyRankDto.SchoolRanking(schoolName, priority);
                })
                .sorted(Comparator.comparingInt(MyRankDto.SchoolRanking::getPriority).reversed())
                .collect(Collectors.toList());

        return new MyRankDto(
                myRank,
                totalApplicants,
                myApplication.getConvertedScore(),
                schoolRankings
        );
    }
}