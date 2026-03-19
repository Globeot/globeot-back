package com.globeot.globeotback.community.repository;

import com.globeot.globeotback.community.domain.Comment;
import com.globeot.globeotback.community.domain.Report;
import com.globeot.globeotback.community.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByArticle_Author_IdAndStatus(Long userId, ReportStatus status);
}