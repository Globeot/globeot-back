package com.globeot.globeotback.application.repository;


import com.globeot.globeotback.application.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByUserId(Long userId);
}