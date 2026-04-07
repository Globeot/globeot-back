package com.globeot.globeotback.community.controller;

import com.globeot.globeotback.community.dto.CommentDto;
import com.globeot.globeotback.community.dto.CommentUpdateDto;
import com.globeot.globeotback.community.service.ArticleService;
import com.globeot.globeotback.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Community - Comments", description = "댓글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final ArticleService articleService;

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDto>> updateComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateDto dto
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(articleService.updateComment(userId, commentId, dto)));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long commentId
    ) {
        articleService.deleteComment(userId, commentId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
