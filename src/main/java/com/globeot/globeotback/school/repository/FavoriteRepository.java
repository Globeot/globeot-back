package com.globeot.globeotback.school.repository;

import com.globeot.globeotback.school.domain.Favorite;
import com.globeot.globeotback.user.dto.MyFavoriteDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndSchoolId(Long userId, Long schoolId);

    Optional<Favorite> findByUser_IdAndSchool_Id(Long userId, Long schoolId);

    void deleteByUser_IdAndSchool_Id(Long userId, Long schoolId);

    @Query("""
    SELECT new com.globeot.globeotback.user.dto.MyFavoriteDto(
        f.id,
        s.id,
        s.name,
        s.city,
        s.country,
        AVG(a.convertedScore),
        s.travelAccessLevel,
        s.monthlyCost,
        s.officialSite
    )
    FROM Favorite f
    JOIN f.school s
    LEFT JOIN Assignment a ON a.school.id = s.id
    WHERE f.user.id = :userId
    GROUP BY f.id, s.id, s.name, s.city, s.country, s.travelAccessLevel, s.monthlyCost, s.officialSite, f.createdAt
    ORDER BY f.createdAt DESC
""")
    List<MyFavoriteDto> findMyFavoriteSchools(@Param("userId") Long userId);

    boolean existsByUser_IdAndSchool_Id(Long userId, Long schoolId);
}