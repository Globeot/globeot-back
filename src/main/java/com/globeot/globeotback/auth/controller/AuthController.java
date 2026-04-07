package com.globeot.globeotback.auth.controller;

import com.globeot.globeotback.auth.dto.LoginRequestDto;
import com.globeot.globeotback.auth.dto.LoginResponseDto;
import com.globeot.globeotback.auth.dto.OtpRequestDto;
import com.globeot.globeotback.auth.dto.OtpVerifyRequestDto;
import com.globeot.globeotback.auth.dto.SignupRequestDto;
import com.globeot.globeotback.auth.dto.SignupResponseDto;
import com.globeot.globeotback.auth.service.AuthService;
import com.globeot.globeotback.global.response.ApiResponse;
import com.globeot.globeotback.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(
            @RequestBody SignupRequestDto request
    ) {
        SignupResponseDto response = authService.signup(request);
        return ResponseEntity.ok(
                ApiResponse.onSuccess("AUTH2001", "회원가입이 완료되었습니다.", response)
        );
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = !userRepository.existsByNickname(nickname);
        return ResponseEntity.ok(
                ApiResponse.onSuccess("AUTH2002", "닉네임 중복 확인에 성공했습니다.", isAvailable)
        );
    }

    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody OtpRequestDto request) {
        authService.sendOtp(request.getEmail());
        return ResponseEntity.ok(
                ApiResponse.onSuccess("AUTH2003", "인증번호가 발송되었습니다.", "인증번호가 발송되었습니다.")
        );
    }

    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody OtpVerifyRequestDto request) {
        authService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );
        return ResponseEntity.ok(
                ApiResponse.onSuccess("AUTH2004", "이메일 인증에 성공했습니다.", "이메일 인증이 완료되었습니다.")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @RequestBody LoginRequestDto request
    ) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.onSuccess("AUTH2005", "로그인에 성공했습니다.", response)
        );
    }
}