package com.globeot.globeotback.application.repository;


import com.globeot.globeotback.application.domain.Application;
import com.globeot.globeotback.application.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByUserId(Long userId);

    Optional<Application> findByUserId(Long userId);

    List<Application> findByStatusOrderByConvertedScoreDesc(Status status);

    void deleteByUser_Id(Long userId);
}