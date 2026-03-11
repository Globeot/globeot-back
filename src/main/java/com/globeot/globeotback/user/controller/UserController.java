package com.globeot.globeotback.user.controller;

import com.globeot.globeotback.auth.jwt.JwtAuthentication;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public User me(
            @AuthenticationPrincipal JwtAuthentication authentication
    ) {

        Long userId = authentication.getUserId();

        return userRepository.findById(userId)
                .orElseThrow();
    }
}