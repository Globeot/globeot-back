package com.globeot.globeotback.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globeot.globeotback.application.domain.Application;
import com.globeot.globeotback.application.dto.ApplicationSubmitDto;
import com.globeot.globeotback.application.enums.Status;
import com.globeot.globeotback.application.repository.ApplicationRepository;
import com.globeot.globeotback.school.repository.SchoolRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;
import com.globeot.globeotback.global.exception.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final SchoolRepository schoolRepository;

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

    private void validateSchoolIds(List<ApplicationSubmitDto.SchoolRequest> schools) {

        List<Long> schoolIds = schools.stream()
                .map(ApplicationSubmitDto.SchoolRequest::getSchool_id)
                .toList();

        List<Long> existingIds = schoolRepository.findAllById(schoolIds)
                .stream()
                .map(s -> s.getId())
                .toList();

        List<Long> invalidIds = schoolIds.stream()
                .filter(id -> !existingIds.contains(id))
                .toList();

        if (!invalidIds.isEmpty()) {
            throw new GlobalExceptionHandler.BadRequestException("존재하지 않는 학교 ID 입니다.");
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
        validateSchoolIds(request.getSchools());

        String imageUrl = s3Service.upload(image);

        ObjectMapper objectMapper = new ObjectMapper();
        String schoolsJson = objectMapper.writeValueAsString(request.getSchools());

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
}