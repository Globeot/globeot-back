package com.globeot.globeotback.user.dto;

public record UserProfileUpdateDto(
        String nickname,
        String exchangeStatus
) {}