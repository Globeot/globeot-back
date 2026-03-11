package com.globeot.globeotback.auth.dto;

import com.globeot.globeotback.user.enums.ExchangeStatus;
import lombok.Getter;

@Getter
public class SignupResponseDto {

    private final ExchangeStatus exchangeStatus;
    private Long userId;
    private String email;
    private String nickname;

    public SignupResponseDto(Long userId, String email, String nickname, ExchangeStatus exchangeStatus, String token) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.exchangeStatus = exchangeStatus;
    }

}