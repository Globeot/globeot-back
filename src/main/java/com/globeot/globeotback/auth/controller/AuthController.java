package com.globeot.globeotback.auth.controller;

import com.globeot.globeotback.auth.dto.*;
import com.globeot.globeotback.auth.service.AuthService;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

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
    public ResponseEntity<Object> signup(
            @RequestBody SignupRequestDto request
    ) {
        SignupResponseDto signupResponse = authService.signup(request);

        return ResponseEntity.ok(
                Map.of(
                        "message", "회원가입이 완료되었습니다.",
                        "data", signupResponse
                )
        );
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = !userRepository.existsByNickname(nickname);
        return ResponseEntity.ok(isAvailable);
    }

    @PostMapping("/email/send")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequestDto request) {
        authService.sendOtp(request.getEmail());
        return ResponseEntity.ok("인증번호가 발송되었습니다.");
    }

    @PostMapping("/email/verify")
    public ResponseEntity<String>verifyOtp(@RequestBody OtpVerifyRequestDto request) {
        authService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );
        return ResponseEntity.ok("이메일 인증에 성공했습니다.");
    }

    @PostMapping("/login")
    public LoginResponseDto login(
            @RequestBody LoginRequestDto request
    ) {
        return authService.login(request);
    }
}