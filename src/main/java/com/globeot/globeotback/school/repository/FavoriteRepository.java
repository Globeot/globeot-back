package com.globeot.globeotback.school.repository;

import com.globeot.globeotback.school.domain.Favorite;
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
        SELECT f.id, s.id, s.name, s.city, s.country, s.avgScore, s.travelAccessLevel, s.monthlyCost, s.officialSite
        FROM Favorite f
        JOIN f.school s
        WHERE f.user.id = :userId
        ORDER BY f.createdAt DESC
    """)
    List<Object[]> findMyFavoriteSchools(@Param("userId") Long userId);

    boolean existsByUser_IdAndSchool_Id(Long userId, Long schoolId);
}