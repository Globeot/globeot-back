package com.globeot.globeotback.auth.dto;

public class LoginResponseDto {

    private Long userId;
    private String token;

    public LoginResponseDto(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }
}