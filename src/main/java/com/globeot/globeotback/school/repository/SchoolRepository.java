package com.globeot.globeotback.school.repository;

import com.globeot.globeotback.school.domain.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    List<School> findByNameContainingIgnoreCase(String keyword);
    List<School> findTop10ByNameContainingIgnoreCaseOrderByNameAsc(String keyword);

}