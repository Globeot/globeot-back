package com.globeot.globeotback.user.service;

import com.globeot.globeotback.application.repository.ApplicationRepository;
import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.ReportStatus;
import com.globeot.globeotback.community.enums.Type;
import com.globeot.globeotback.community.repository.ArticleRepository;
import com.globeot.globeotback.community.repository.CommentRepository;
import com.globeot.globeotback.community.repository.ReportRepository;
import com.globeot.globeotback.community.repository.ScrapRepository;
import com.globeot.globeotback.global.exception.CustomException;
import com.globeot.globeotback.global.exception.ErrorCode;
import com.globeot.globeotback.school.repository.FavoriteRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.dto.*;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import com.globeot.globeotback.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    public UserService(UserRepository userRepository,
                       ArticleRepository articleRepository,
                       ReportRepository reportRepository,
                       CommentRepository commentRepository,
                       ScrapRepository scrapRepository,
                       FavoriteRepository favoriteRepository,
                       ApplicationRepository applicationRepository) {
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        boolean hasActiveSaleOrRecruiting = articleRepository.existsByAuthor_IdAndArticleStatusIn(
                userId,
                List.of(ArticleStatus.OPEN, ArticleStatus.RESERVED, ArticleStatus.RECRUITING)
        );

        boolean hasPendingReport = reportRepository.existsByArticle_Author_IdAndStatus(userId, ReportStatus.PENDING);

        if (hasActiveSaleOrRecruiting) {
            throw new CustomException(ErrorCode.CANNOT_WITHDRAW_ACTIVE_ARTICLE);
        }

        if (hasPendingReport) {
            throw new CustomException(ErrorCode.CANNOT_WITHDRAW_PENDING_REPORT);
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (Type) row[3],
                        (ArticleStatus) row[4],
                        (java.time.LocalDateTime) row[5],
                        (Long) row[6]
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MyCommentDto> getMyComments(Long userId) {
        List<Object[]> results = commentRepository.findMyCommentsWithArticleTitle(userId);

        return results.stream()
                .map(row -> new MyCommentDto(
                        (Long) row[0],
                        (String) row[1],
                        (String) row[2],
                        (java.time.LocalDateTime) row[3]
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MyScrapDto> getMyScraps(Long userId) {
        List<Object[]> results = scrapRepository.findMyScrapsWithArticle(userId);

        return results.stream()
                .map(row -> {
                    Long articleId = (Long) row[1];
                    Long commentCount = commentRepository.countByArticle_Id(articleId);

                    return new MyScrapDto(
                            (Long) row[0],
                            articleId,
                            (String) row[2],
                            (String) row[3],
                            (Type) row[4],
                            (ArticleStatus) row[5],
                            (java.time.LocalDateTime) row[6],
                            commentCount
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MyFavoriteDto> getMyFavoriteSchools(Long userId) {
        return favoriteRepository.findMyFavoriteSchools(userId);
    }
}