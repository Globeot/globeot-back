package com.globeot.globeotback.application.repository;


import com.globeot.globeotback.application.domain.Assignment;
import com.globeot.globeotback.school.dto.AssignmentHistoryDto;
import com.globeot.globeotback.school.dto.SchoolScoreDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("""
    SELECT new com.globeot.globeotback.school.dto.AssignmentHistoryDto(
        a.semester,
        a.convertedScore
    )
    FROM Assignment a
    WHERE a.school.id = :schoolId
    ORDER BY a.semester DESC, a.convertedScore DESC
""")
    List<AssignmentHistoryDto> findSchoolHistoryBySchoolId(@Param("schoolId") Long schoolId);

    @Query("""
        SELECT new com.globeot.globeotback.school.dto.SchoolScoreDto(
            AVG(a.convertedScore),
            MIN(a.convertedScore),
            MAX(a.convertedScore)
        )
        FROM Assignment a
        WHERE a.school.id = :schoolId
    """)
    SchoolScoreDto findScoreStatsBySchoolId(@Param("schoolId") Long schoolId);
}