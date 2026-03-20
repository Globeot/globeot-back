package com.globeot.globeotback.user.controller;

import com.globeot.globeotback.auth.jwt.JwtAuthentication;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.dto.UserProfileDto;
import com.globeot.globeotback.user.dto.UserProfileUpdateDto;
import com.globeot.globeotback.user.repository.UserRepository;

import com.globeot.globeotback.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/me")
    public User me(@AuthenticationPrincipal Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/me")
    public String withdraw(@AuthenticationPrincipal Long userId) {

        if (userId == null) {
            throw new RuntimeException("인증 정보 없음");
        }

        userService.withdrawUser(userId);

        return "회원 탈퇴가 완료되었습니다.";
    }

    @GetMapping("/profile")
    public UserProfileDto getProfile(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new RuntimeException("인증 정보 없음");
        }
        return userService.getUserProfile(userId);
    }

    @PatchMapping("/profile")
    public UserProfileDto updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserProfileUpdateDto dto
    ) {
        if (userId == null) {
            throw new RuntimeException("인증 정보 없음");
        }
        return userService.updateUserProfile(userId, dto);
    }
}