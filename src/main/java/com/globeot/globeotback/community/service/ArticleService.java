package com.globeot.globeotback.community.service;

import com.globeot.globeotback.community.domain.Article;
import com.globeot.globeotback.community.domain.Comment;
import com.globeot.globeotback.community.domain.Report;
import com.globeot.globeotback.community.domain.Scrap;
import com.globeot.globeotback.community.dto.*;
import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.Region;
import com.globeot.globeotback.community.enums.ReportStatus;
import com.globeot.globeotback.community.enums.Type;
import com.globeot.globeotback.community.repository.*;
import com.globeot.globeotback.school.domain.School;
import com.globeot.globeotback.school.repository.SchoolRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import com.globeot.globeotback.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final ScrapRepository scrapRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;

    public ArticleService(ArticleRepository articleRepository,
                          CommentRepository commentRepository,
                          ScrapRepository scrapRepository,
                          ReportRepository reportRepository,
                          UserRepository userRepository,
                          SchoolRepository schoolRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.scrapRepository = scrapRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
    }

    // ── 게시글 목록 조회 + 검색 ──────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ArticlePageDto getArticles(String keyword,
                                      ExchangeStatus exchangeStatus,
                                      Region region,
                                      Type type,
                                      String topic,
                                      int page,
                                      int size) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim().replaceAll("\\s+", "");
        Page<Object[]> pageResult = articleRepository.findArticlesWithFilter(
                kw, exchangeStatus, region, type, topic, PageRequest.of(page, size));

        List<ArticleListDto> content = pageResult.getContent().stream().map(row -> {
            Article a = (Article) row[0];
            Long commentCount = (Long) row[1];
            return new ArticleListDto(
                    a.getId(),
                    a.getTitle(),
                    getNickname(a.getAuthor()),
                    a.getExchangeStatus(),
                    a.getRegion(),
                    a.getType(),
                    a.getCreatedAt(),
                    commentCount,
                    a.getTopic(),
                    a.getArticleStatus()
            );
        }).toList();

        return new ArticlePageDto(content, pageResult.getTotalPages(), pageResult.getTotalElements());
    }

    // ── 조회수 증가 ──────────────────────────────────────────────────────────
    @Transactional
    public void incrementViewCount(Long articleId) {
        Article article = findArticle(articleId);
        article.setViewCount(article.getViewCount() == null ? 1 : article.getViewCount() + 1);
    }

    // ── 게시글 상세 조회 ─────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ArticleDetailDto getArticle(Long userId, Long articleId) {
        Article article = findArticle(articleId);

        long commentCount = commentRepository.countByArticle_Id(articleId);
        boolean isAuthor = userId != null && userId.equals(article.getAuthor().getId());
        boolean isScrapped = userId != null && scrapRepository.existsByUser_IdAndArticle_Id(userId, articleId);

        return toDetailDto(article, commentCount, isAuthor, isScrapped);
    }

    // ── 게시글 작성 ──────────────────────────────────────────────────────────
    @Transactional
    public ArticleDetailDto createArticle(Long userId, ArticleCreateDto dto) {
        User author = findUser(userId);
        School school = dto.getSchoolId() != null
                ? schoolRepository.findById(dto.getSchoolId()).orElse(null)
                : null;
        ArticleStatus initialStatus = null;
        if (dto.getType() == Type.SALE) initialStatus = ArticleStatus.OPEN;
        else if (dto.getType() == Type.COMPANION) initialStatus = ArticleStatus.RECRUITING;

        Article article = Article.builder()
                .author(author)
                .title(dto.getTitle())
                .content(dto.getContent())
                .region(dto.getRegion())
                .type(dto.getType())
                .exchangeStatus(dto.getExchangeStatus())
                .topic(dto.getTopic())
                .school(school)
                .imageUrls(dto.getImageUrls())
                .articleStatus(initialStatus)
                .viewCount(0)
                .reportCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        articleRepository.save(article);
        return toDetailDto(article, 0L, true, false);
    }

    // ── 게시글 수정 ──────────────────────────────────────────────────────────
    @Transactional
    public ArticleDetailDto updateArticle(Long userId, Long articleId, ArticleUpdateDto dto) {
        Article article = findArticle(articleId);
        checkAuthor(userId, article.getAuthor().getId());

        if (dto.getTitle() != null) article.setTitle(dto.getTitle());
        if (dto.getContent() != null) article.setContent(dto.getContent());
        if (dto.getRegion() != null) article.setRegion(dto.getRegion());
        if (dto.getType() != null) article.setType(dto.getType());
        if (dto.getExchangeStatus() != null) article.setExchangeStatus(dto.getExchangeStatus());
        if (dto.getTopic() != null) article.setTopic(dto.getTopic());
        if (dto.getSchoolId() != null) {
            School school = schoolRepository.findById(dto.getSchoolId()).orElse(null);
            article.setSchool(school);
        }
        if (dto.getImageUrls() != null) article.setImageUrls(dto.getImageUrls().isEmpty() ? null : dto.getImageUrls());
        if (dto.getArticleStatus() != null) article.setArticleStatus(dto.getArticleStatus());
        article.setUpdatedAt(LocalDateTime.now());

        long commentCount = commentRepository.countByArticle_Id(articleId);
        return toDetailDto(article, commentCount, true, scrapRepository.existsByUser_IdAndArticle_Id(userId, articleId));
    }

    // ── 게시글 삭제 ──────────────────────────────────────────────────────────
    @Transactional
    public void deleteArticle(Long userId, Long articleId) {
        Article article = findArticle(articleId);
        checkAuthor(userId, article.getAuthor().getId());
        commentRepository.deleteAllByArticle_Id(articleId);
        articleRepository.delete(article);
    }

    // ── 게시글 스크랩 ────────────────────────────────────────────────────────
    @Transactional
    public void scrapArticle(Long userId, Long articleId) {
        if (scrapRepository.existsByUser_IdAndArticle_Id(userId, articleId)) {
            throw new IllegalStateException("이미 스크랩한 게시글입니다.");
        }
        User user = findUser(userId);
        Article article = findArticle(articleId);
        Scrap scrap = Scrap.builder()
                .user(user)
                .article(article)
                .createdAt(LocalDateTime.now())
                .build();
        scrapRepository.save(scrap);
    }

    // ── 게시글 스크랩 취소 ───────────────────────────────────────────────────
    @Transactional
    public void unscrapArticle(Long userId, Long articleId) {
        Scrap scrap = scrapRepository.findByUser_IdAndArticle_Id(userId, articleId)
                .orElseThrow(() -> new IllegalArgumentException("스크랩 내역이 없습니다."));
        scrapRepository.delete(scrap);
    }

    // ── 게시글 신고 ──────────────────────────────────────────────────────────
    @Transactional
    public void reportArticle(Long userId, Long articleId, ReportCreateDto dto) {
        if (reportRepository.existsByUser_IdAndArticle_Id(userId, articleId)) {
            throw new IllegalStateException("이미 신고한 게시글입니다.");
        }
        User user = findUser(userId);
        Article article = findArticle(articleId);

        if (userId.equals(article.getAuthor().getId())) {
            throw new IllegalArgumentException("본인 게시글은 신고할 수 없습니다.");
        }

        Report report = Report.builder()
                .user(user)
                .article(article)
                .reason(dto.getReason())
                .status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        reportRepository.save(report);

        article.setReportCount(article.getReportCount() == null ? 1 : article.getReportCount() + 1);
    }

    // ── 댓글 목록 조회 ───────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long userId, Long articleId) {
        findArticle(articleId);
        List<Comment> comments = commentRepository.findAllByArticle_IdOrderByCreatedAtAsc(articleId);
        return comments.stream().map(c -> toCommentDto(c, userId)).toList();
    }

    // ── 댓글 작성 ────────────────────────────────────────────────────────────
    @Transactional
    public CommentDto createComment(Long userId, Long articleId, CommentCreateDto dto) {
        User user = findUser(userId);
        Article article = findArticle(articleId);

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        }

        Comment comment = Comment.builder()
                .user(user)
                .article(article)
                .parent(parent)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        return toCommentDto(comment, userId);
    }

    // ── 댓글 수정 ────────────────────────────────────────────────────────────
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, CommentUpdateDto dto) {
        Comment comment = findComment(commentId);
        checkAuthor(userId, comment.getUser().getId());
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return toCommentDto(comment, userId);
    }

    // ── 댓글 삭제 ────────────────────────────────────────────────────────────
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = findComment(commentId);
        checkAuthor(userId, comment.getUser().getId());
        commentRepository.delete(comment);
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────────────────────

    private Article findArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private String getNickname(User user) {
        return user.isDeleted() ? "탈퇴한 사용자" : user.getNickname();
    }

    private void checkAuthor(Long userId, Long authorId) {
        if (!userId.equals(authorId)) {
            throw new IllegalStateException("권한이 없습니다.");
        }
    }

    private ArticleDetailDto toDetailDto(Article a, long commentCount, boolean isAuthor, boolean isScrapped) {
        boolean isBlinded = a.getReportCount() != null && a.getReportCount() >= 5;
        return new ArticleDetailDto(
                a.getId(),
                a.getTitle(),
                a.getContent(),
                a.getAuthor().getId(),
                getNickname(a.getAuthor()),
                a.getExchangeStatus(),
                a.getRegion(),
                a.getType(),
                a.getViewCount(),
                commentCount,
                a.getCreatedAt(),
                a.getUpdatedAt(),
                a.getTopic(),
                a.getSchool() != null ? a.getSchool().getId() : null,
                a.getSchool() != null ? a.getSchool().getName() : null,
                a.getImageUrls(),
                a.getArticleStatus(),
                isAuthor,
                isScrapped,
                isBlinded
        );
    }

    private CommentDto toCommentDto(Comment c, Long userId) {
        return new CommentDto(
                c.getId(),
                c.getUser().getId(),
                getNickname(c.getUser()),
                c.getContent(),
                c.getParent() != null ? c.getParent().getId() : null,
                c.getCreatedAt(),
                c.getUpdatedAt(),
                userId != null && userId.equals(c.getUser().getId())
        );
    }
}
