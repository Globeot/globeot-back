package com.globeot.globeotback.user.service;
import com.globeot.globeotback.application.repository.ApplicationRepository;
import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.ReportStatus;
import com.globeot.globeotback.community.enums.Type;
import com.globeot.globeotback.community.repository.ArticleRepository;
import com.globeot.globeotback.community.repository.CommentRepository;
import com.globeot.globeotback.community.repository.ReportRepository;
import com.globeot.globeotback.community.repository.ScrapRepository;
import com.globeot.globeotback.school.repository.FavoriteRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.dto.*;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import com.globeot.globeotback.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final ScrapRepository scrapRepository;
    private final FavoriteRepository favoriteRepository;
    private final ApplicationRepository applicationRepository;

    public UserService(UserRepository userRepository, ArticleRepository articleRepository, ReportRepository reportRepository, CommentRepository commentRepository, ScrapRepository scrapRepository, FavoriteRepository favoriteRepository, ApplicationRepository applicationRepository ) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.reportRepository = reportRepository;
        this.commentRepository = commentRepository;
        this.scrapRepository = scrapRepository;
        this.favoriteRepository = favoriteRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
    }

    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean hasActiveSaleOrRecruiting = articleRepository.existsByAuthor_IdAndArticleStatusIn(
                userId,
                List.of(ArticleStatus.OPEN, ArticleStatus.RESERVED, ArticleStatus.RECRUITING)
        );

        boolean hasPendingReport = reportRepository.existsByArticle_Author_IdAndStatus(userId, ReportStatus.PENDING);

        if (hasActiveSaleOrRecruiting) {
            throw new IllegalStateException(
                    "진행 중인 거래 또는 동행 게시글이 있어 탈퇴할 수 없습니다. 게시글을 먼저 종료해주세요."
            );
        }else if (hasPendingReport) {
            throw new IllegalStateException(
                    "신고 처리 중인 게시글이 있어 탈퇴할 수 없습니다. 신고 내역 처리를 기다려주세요."
            );
        }
        applicationRepository.deleteByUser_Id(userId);

        LocalDateTime now = LocalDateTime.now();
        user.softDelete();
        user.getAuthAccounts().forEach(auth -> auth.setDeletedAt(now));

        userRepository.save(user);

    }

    @Transactional
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return new UserProfileDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getExchangeStatus()
        );
    }

    @Transactional
    public UserProfileDto updateUserProfile(Long userId, UserProfileUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (dto.nickname() != null) {
            user.setNickname(dto.nickname());
        }

        if (dto.exchangeStatus() != null) {
            user.setExchangeStatus(ExchangeStatus.valueOf(dto.exchangeStatus()));
        }

        userRepository.save(user);

        return new UserProfileDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getExchangeStatus()
        );
    }

    @Transactional
    public List<MyArticleDto> getMyArticles(Long userId) {
        List<Object[]> results = articleRepository.findMyArticlesWithCommentCount(userId);

        return results.stream()
                .map(row -> new MyArticleDto(
                        (Long) row[0],                 // articleId
                        (String) row[1],               // title
                        (String) row[2],               // content
                        (Type) row[3],               // type
                        (ArticleStatus) row[4],        // articleStatus
                        (java.time.LocalDateTime) row[5], // createdAt
                        ((Long) row[6])                // commentCount
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MyCommentDto> getMyComments(Long userId) {
        List<Object[]> results = commentRepository.findMyCommentsWithArticleTitle(userId);

        return results.stream()
                .map(row -> new MyCommentDto(
                        (Long) row[0],         // articleId
                        (String) row[1],       // title
                        (String) row[2],       // content
                        (java.time.LocalDateTime) row[3] // createdAt
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MyScrapDto> getMyScraps(Long userId) {
        List<Object[]> results = scrapRepository.findMyScrapsWithArticle(userId);

        return results.stream()
                .map(row -> {
                    Long articleId = (Long) row[1];
                    Long commentCount = commentRepository.countByArticle_Id(articleId); // 댓글 수 조회
                    return new MyScrapDto(
                            (Long) row[0],               // scrapId
                            articleId,                   // articleId
                            (String) row[2],             // title
                            (String) row[3],             // content
                            (com.globeot.globeotback.community.enums.Type) row[4], // type
                            (com.globeot.globeotback.community.enums.ArticleStatus) row[5], // articleStatus
                            (java.time.LocalDateTime) row[6], // createdAt
                            commentCount                  // commentCount
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MyFavoriteDto> getMyFavoriteSchools(Long userId) {
        List<Object[]> results = favoriteRepository.findMyFavoriteSchools(userId);

        return results.stream()
                .map(row -> new MyFavoriteDto(
                        (Long) row[0],                  // favoriteId
                        (Long) row[1],                  // schoolId
                        (String) row[2],                // name
                        (String) row[3],                // city
                        (String) row[4],                // country
                        (Double) row[5],                // avgScore
                        (com.globeot.globeotback.school.enums.Level) row[6], // travelAccessLevel
                        (String) row[7],                // monthlyCost
                        (String) row[8]                 // officialSite
                ))
                .collect(Collectors.toList());
    }

}