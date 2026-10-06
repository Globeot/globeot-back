package com.globeot.globeotback.auth.dto;

public class LoginResponseDto {

    private Long userId;
    private String token;
    private boolean termsAgreed;

    public LoginResponseDto(
            Long userId,
            String token,
            boolean termsAgreed
    ) {
        this.userId = userId;
        this.token = token;
        this.termsAgreed = termsAgreed;
    }

    public Long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public boolean isTermsAgreed() {
        return termsAgreed;
    }
}