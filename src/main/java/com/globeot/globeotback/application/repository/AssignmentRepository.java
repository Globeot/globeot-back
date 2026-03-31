package com.globeot.globeotback.application.repository;


import com.globeot.globeotback.application.domain.Assignment;
import com.globeot.globeotback.school.dto.AssignmentHistoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("""
        SELECT new com.globeot.globeotback.school.dto.AssignmentHistoryDto(
            a.application.semester,
            a.application.convertedScore
        )
        FROM Assignment a
        WHERE a.school.id = :schoolId
        ORDER BY a.application.semester DESC, a.application.convertedScore DESC
    """)
    List<AssignmentHistoryDto> findSchoolHistoryBySchoolId(@Param("schoolId") Long schoolId);
}