package com.globeot.globeotback.community.controller;

import com.globeot.globeotback.community.dto.*;
import com.globeot.globeotback.community.enums.Region;
import com.globeot.globeotback.community.enums.Type;
import com.globeot.globeotback.community.service.ArticleService;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Community - Articles", description = "게시글 API")
@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 게시글 목록 조회 + 검색
    @GetMapping
    public ArticlePageDto getArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ExchangeStatus exchangeStatus,
            @RequestParam(required = false) Region region,
            @RequestParam(required = false) Type type,
            @RequestParam(required = false) String topic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return articleService.getArticles(keyword, exchangeStatus, region, type, topic, page, size);
    }

    // 조회수 증가
    @PostMapping("/{articleId}/view")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void incrementViewCount(@PathVariable Long articleId) {
        articleService.incrementViewCount(articleId);
    }

    // 게시글 상세 조회
    @GetMapping("/{articleId}")
    public ArticleDetailDto getArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        return articleService.getArticle(userId, articleId);
    }

    // 게시글 작성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDetailDto createArticle(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ArticleCreateDto dto
    ) {
        return articleService.createArticle(userId, dto);
    }

    // 게시글 수정
    @PatchMapping("/{articleId}")
    public ArticleDetailDto updateArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId,
            @RequestBody ArticleUpdateDto dto
    ) {
        return articleService.updateArticle(userId, articleId, dto);
    }

    // 게시글 삭제
    @DeleteMapping("/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        articleService.deleteArticle(userId, articleId);
    }

    // 게시글 스크랩
    @PostMapping("/{articleId}/scrap")
    @ResponseStatus(HttpStatus.CREATED)
    public void scrapArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        articleService.scrapArticle(userId, articleId);
    }

    // 게시글 스크랩 취소
    @DeleteMapping("/{articleId}/scrap")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unscrapArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        articleService.unscrapArticle(userId, articleId);
    }

    // 게시글 신고
    @PostMapping("/{articleId}/report")
    @ResponseStatus(HttpStatus.CREATED)
    public void reportArticle(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId,
            @Valid @RequestBody ReportCreateDto dto
    ) {
        articleService.reportArticle(userId, articleId, dto);
    }

    // 댓글 목록 조회
    @GetMapping("/{articleId}/comments")
    public List<CommentDto> getComments(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId
    ) {
        return articleService.getComments(userId, articleId);
    }

    // 댓글 작성
    @PostMapping("/{articleId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long articleId,
            @Valid @RequestBody CommentCreateDto dto
    ) {
        return articleService.createComment(userId, articleId, dto);
    }
}
