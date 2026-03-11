package com.globeot.globeotback.auth.repository;

import com.globeot.globeotback.auth.domain.AuthAccount;
import com.globeot.globeotback.auth.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {

    Optional<AuthAccount> findByProviderAndProviderUserId(
            AuthProvider provider,
            String providerUserId
    );
}