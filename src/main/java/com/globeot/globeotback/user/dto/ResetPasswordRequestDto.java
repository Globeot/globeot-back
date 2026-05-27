package com.globeot.globeotback.user.dto;

import lombok.Getter;

@Getter
public class ResetPasswordRequestDto {

    private String newPassword;
    private String confirmPassword;
}