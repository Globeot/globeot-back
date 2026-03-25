package com.globeot.globeotback.application.repository;


import com.globeot.globeotback.application.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByUserId(Long userId);

    List<Application> findAllByOrderByConvertedScoreDesc();
}