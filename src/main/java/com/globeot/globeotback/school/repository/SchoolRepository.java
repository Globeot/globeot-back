package com.globeot.globeotback.school.repository;

import com.globeot.globeotback.school.domain.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {
}