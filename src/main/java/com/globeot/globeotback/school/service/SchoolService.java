package com.globeot.globeotback.school.service;

import com.globeot.globeotback.school.domain.School;
import com.globeot.globeotback.school.dto.SchoolSearchDto;
import com.globeot.globeotback.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;

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
}