package com.globeot.globeotback.school.repository;

import com.globeot.globeotback.school.domain.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    List<School> findByNameContainingIgnoreCase(String keyword);
    List<School> findTop10ByNameContainingIgnoreCaseOrderByNameAsc(String keyword);

    @Query("""
        SELECT s
        FROM School s
        WHERE
            (
                :keyword IS NULL
                OR :keyword = ''
                OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.country) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:minScore IS NULL OR s.avgScore >= :minScore)
            AND (:maxScore IS NULL OR s.avgScore <= :maxScore)
        ORDER BY s.name ASC
    """)
    List<School> findByKeywordAndScoreRange(
            @Param("keyword") String keyword,
            @Param("minScore") Double minScore,
            @Param("maxScore") Double maxScore
    );

}