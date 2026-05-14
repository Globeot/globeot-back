package com.globeot.globeotback.school.repository;

import com.globeot.globeotback.school.domain.School;
import com.globeot.globeotback.school.dto.SchoolListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    List<School> findTop10ByNameContainingIgnoreCaseOrderByNameAsc(String keyword);

    @Query("""
        SELECT new com.globeot.globeotback.school.dto.SchoolListDto(
            s.id,
            s.country,
            s.city,
            s.name,
            AVG(a.convertedScore),
            s.travelAccessLevel,
            s.monthlyCost,
            s.officialSite
        )
        FROM School s
        LEFT JOIN Assignment a ON a.school.id = s.id
        WHERE (:keyword IS NULL OR :keyword = '' 
               OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.country) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%')))
        GROUP BY s.id, s.country, s.city, s.name, s.travelAccessLevel, s.monthlyCost, s.officialSite
        HAVING (
            (:noScoreOnly = true AND COUNT(a) = 0)
            OR
            (:noScoreOnly = false
                AND (:minScore IS NULL OR AVG(a.convertedScore) >= :minScore)
                AND (:maxScore IS NULL OR AVG(a.convertedScore) <= :maxScore)
            )
        )
        ORDER BY AVG(a.convertedScore) DESC
    """)
    List<SchoolListDto> findByKeywordAndScoreRange(
            @Param("keyword") String keyword,
            @Param("minScore") Double minScore,
            @Param("maxScore") Double maxScore,
            @Param("noScoreOnly") boolean noScoreOnly
    );

}