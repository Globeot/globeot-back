package com.globeot.globeotback.user.controller;

import com.globeot.globeotback.global.exception.CustomException;
import com.globeot.globeotback.global.exception.ErrorCode;
import com.globeot.globeotback.global.response.ApiResponse;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.dto.MyArticleDto;
import com.globeot.globeotback.user.dto.MyCommentDto;
import com.globeot.globeotback.user.dto.MyFavoriteDto;
import com.globeot.globeotback.user.dto.MyScrapDto;
import com.globeot.globeotback.user.dto.UserProfileDto;
import com.globeot.globeotback.user.dto.UserProfileUpdateDto;
import com.globeot.globeotback.user.repository.UserRepository;
import com.globeot.globeotback.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ApiResponse<User>> me(@AuthenticationPrincipal Long userId) {
        validateUserId(userId);
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(user));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<String>> withdraw(@AuthenticationPrincipal Long userId) {
        validateUserId(userId);
        userService.withdrawUser(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess("회원 탈퇴가 완료되었습니다."));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(@AuthenticationPrincipal Long userId) {
        validateUserId(userId);
        UserProfileDto response = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UserProfileUpdateDto dto
    ) {
        validateUserId(userId);
        UserProfileDto response = userService.updateUserProfile(userId, dto);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/articles")
    public ResponseEntity<ApiResponse<List<MyArticleDto>>> getMyArticles(@AuthenticationPrincipal Long userId) {
        validateUserId(userId);
        List<MyArticleDto> response = userService.getMyArticles(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<List<MyCommentDto>>> getMyComments(@AuthenticationPrincipal Long userId) {
        validateUserId(userId);
        List<MyCommentDto> response = userService.getMyComments(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/scraps")
    public ResponseEntity<ApiResponse<List<MyScrapDto>>> getMyScraps(@AuthenticationPrincipal Long userId) {
        validateUserId(userId);
        List<MyScrapDto> response = userService.getMyScraps(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<MyFavoriteDto>>> getMyFavoriteSchools(@AuthenticationPrincipal Long userId) {
        validateUserId(userId);
        List<MyFavoriteDto> response = userService.getMyFavoriteSchools(userId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
    }
}