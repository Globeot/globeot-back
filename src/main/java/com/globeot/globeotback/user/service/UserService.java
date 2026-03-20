package com.globeot.globeotback.user.service;
import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.ReportStatus;
import com.globeot.globeotback.community.repository.ArticleRepository;
import com.globeot.globeotback.community.repository.CommentRepository;
import com.globeot.globeotback.community.repository.ReportRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ReportRepository reportRepository;

    public UserService(UserRepository userRepository, ArticleRepository articleRepository,ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.reportRepository = reportRepository;

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
        LocalDateTime now = LocalDateTime.now();
        user.softDelete();
        user.getAuthAccounts().forEach(auth -> auth.setDeletedAt(now));

        userRepository.save(user);

    }

}