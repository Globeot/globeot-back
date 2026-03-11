package com.globeot.globeotback.auth.dto;

import com.globeot.globeotback.user.enums.ExchangeStatus;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    private String email;
    private String password;
    private String nickname;
    private ExchangeStatus exchangeStatus;

}