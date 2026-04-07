package com.globeot.globeotback.community.controller;

import com.globeot.globeotback.community.dto.*;
import com.globeot.globeotback.community.enums.Region;
import com.globeot.globeotback.community.enums.Type;
import com.globeot.globeotback.community.service.ArticleService;
import com.globeot.globeotback.global.response.ApiResponse;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Community - Articles", description = "게시글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    // 게시글 목록 조회 + 검색
    @GetMapping
    public ResponseEntity<ApiResponse<ArticlePageDto>> getArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ExchangeStatus exchangeStatus,
            @RequestParam(required = false) Region region,
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) String topic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
                articleService.getArticles(keyword, exchangeStatus, region, type, topic, page, size)));
    }

    // 조회수 증가
    @PostMapping("/{articleId}/view")
    public ResponseEntity<ApiResponse<Void>> incrementViewCount(@PathVariable Long articleId) {
        articleService.incrementViewCount(articleId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 게시글 상세 조회
    @GetMapping("/{articleId}")
    public ResponseEntity<ApiResponse<ArticleDetailDto>> getArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(articleService.getArticle(userId, articleId)));
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<ArticleDetailDto>> createArticle(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ArticleCreateDto dto
    ) {
        return ResponseEntity.status(201).body(ApiResponse.onSuccess(articleService.createArticle(userId, dto)));
    }

    // 게시글 수정
    @PatchMapping("/{articleId}")
    public ResponseEntity<ApiResponse<ArticleDetailDto>> updateArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId,
            @RequestBody ArticleUpdateDto dto
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(articleService.updateArticle(userId, articleId, dto)));
    }

    // 게시글 삭제
    @DeleteMapping("/{articleId}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        articleService.deleteArticle(userId, articleId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 게시글 스크랩
    @PostMapping("/{articleId}/scrap")
    public ResponseEntity<ApiResponse<Void>> scrapArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        articleService.scrapArticle(userId, articleId);
        return ResponseEntity.status(201).body(ApiResponse.onSuccess(null));
    }

    // 게시글 스크랩 취소
    @DeleteMapping("/{articleId}/scrap")
    public ResponseEntity<ApiResponse<Void>> unscrapArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        articleService.unscrapArticle(userId, articleId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 게시글 신고
    @PostMapping("/{articleId}/report")
    public ResponseEntity<ApiResponse<Void>> reportArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId,
            @Valid @RequestBody ReportCreateDto dto
    ) {
        articleService.reportArticle(userId, articleId, dto);
        return ResponseEntity.status(201).body(ApiResponse.onSuccess(null));
    }

    // 댓글 목록 조회
    @GetMapping("/{articleId}/comments")
    public ResponseEntity<ApiResponse<List<CommentDto>>> getComments(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(articleService.getComments(userId, articleId)));
    }

    // 댓글 작성
    @PostMapping("/{articleId}/comments")
    public ResponseEntity<ApiResponse<CommentDto>> createComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId,
            @Valid @RequestBody CommentCreateDto dto
    ) {
        return ResponseEntity.status(201).body(ApiResponse.onSuccess(articleService.createComment(userId, articleId, dto)));
    }
}
