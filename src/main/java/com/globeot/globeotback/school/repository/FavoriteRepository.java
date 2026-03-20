package com.globeot.globeotback.school.repository;

import com.globeot.globeotback.school.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query("""
        SELECT f.id, s.id, s.name, s.city, s.country, s.avgScore, s.travelAccessLevel, s.monthlyCost, s.officialSite
        FROM Favorite f
        JOIN f.school s
        WHERE f.user.id = :userId
        ORDER BY f.createdAt DESC
    """)
    List<Object[]> findMyFavoriteSchools(@Param("userId") Long userId);
}