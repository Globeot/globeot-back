package com.globeot.globeotback.auth.domain;

import com.globeot.globeotback.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "auth_accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_provider_provider_user_id", columnNames = {"provider", "provider_user_id"}),
                @UniqueConstraint(name = "uk_user_provider", columnNames = {"user_id", "provider"})
        }
)
public class AuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 어떤 유저의 로그인 수단인지 연결
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * LOCAL / GOOGLE
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    /**
     * provider 측 고유 ID
     * GOOGLE이면 구글 sub
     * LOCAL이면 email 또는 내부 식별값
     */
    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    /**
     * LOCAL일 때만 사용
     * GOOGLE은 null 가능
     */
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected AuthAccount() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public AuthAccount(AuthProvider provider, String providerUserId, String passwordHash) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.passwordHash = passwordHash;
    }

    public static AuthAccount createLocalAccount(String email, String passwordHash) {
        return new AuthAccount(AuthProvider.LOCAL, email, passwordHash);
    }

    public static AuthAccount createGoogleAccount(String googleSub) {
        return new AuthAccount(AuthProvider.GOOGLE, googleSub, null);
    }

    public void assignUser(User user) {
        this.user = user;
    }

    public void unassignUser() {
        this.user = null;
    }

    public void updatePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean isLocalAccount() {
        return this.provider == AuthProvider.LOCAL;
    }

    public boolean isGoogleAccount() {
        return this.provider == AuthProvider.GOOGLE;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
