package com.globeot.globeotback.user.dto;

public record UserProfileDto(
        Long userId,
        String nickname,
        String email,
        com.globeot.globeotback.user.enums.ExchangeStatus exchangeStatus
) {}