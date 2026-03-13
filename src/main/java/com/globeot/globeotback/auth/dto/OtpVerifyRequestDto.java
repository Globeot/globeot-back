package com.globeot.globeotback.auth.dto;

public class OtpVerifyRequestDto {

    private String email;
    private String otp;

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }
}