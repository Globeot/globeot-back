package com.globeot.globeotback.auth.domain;

import com.globeot.globeotback.auth.enums.AuthProvider;
import com.globeot.globeotback.user.domain.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "auth_accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_provider_provider_user_id",
                        columnNames = {"provider", "provider_user_id"}
                ),
                @UniqueConstraint(
                        name = "uk_user_provider",
                        columnNames = {"user_id", "provider"}
                )
        }
)
public class AuthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
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
     * provider 측 고유 사용자 식별값
     * LOCAL  -> email
     * GOOGLE -> google sub
     */
    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    /**
     * LOCAL 계정 비밀번호 해시
     * GOOGLE 계정은 null 가능
     */
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected AuthAccount() {
    }

    private AuthAccount(AuthProvider provider, String providerUserId, String passwordHash) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.passwordHash = passwordHash;
    }

    public AuthAccount(AuthProvider provider, String providerUserId, String passwordHash, User user) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.passwordHash = passwordHash;
        this.user = user;
    }


    public static AuthAccount createLocalAccount(String email, String passwordHash) {
        AuthAccount account = new AuthAccount();
        account.provider = AuthProvider.valueOf("LOCAL");             // 반드시 NON NULL
        account.providerUserId = email;         // 일반적으로 이메일을 USER_ID로 사용
        account.passwordHash = passwordHash;
        return account;
    }

    public static AuthAccount createGoogleAccount(String googleSub) {
        return new AuthAccount(AuthProvider.GOOGLE, googleSub, null);
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}