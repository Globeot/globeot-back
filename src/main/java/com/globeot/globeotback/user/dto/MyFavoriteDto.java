package com.globeot.globeotback.user.dto;

import com.globeot.globeotback.school.enums.Level;

public record MyFavoriteDto(
        Long favoriteId,
        Long schoolId,
        String name,
        String city,
        String country,
        Double avgScore,
        Level travelAccessLevel,
        String monthlyCost,
        String officialSite
) {}