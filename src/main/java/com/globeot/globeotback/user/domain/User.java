package com.globeot.globeotback.user.domain;

import com.globeot.globeotback.auth.domain.AuthAccount;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import com.globeot.globeotback.user.enums.UserRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /**
     * 학교 이메일
     */
    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 30)
    private String nickname;

    private LocalDateTime nicknameUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_status", nullable = false, length = 30)
    private ExchangeStatus exchangeStatus;

    /**
     * USER / ADMIN
     * 추후 권한 확장용
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;

    /**
     * 이메일 인증 여부
     */
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    /**
     * 계정 활성 여부
     * 회원 탈퇴 시 false 처리
     */
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuthAccount> authAccounts = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected User() {
    }

    public User(String email, String nickname, ExchangeStatus exchangeStatus) {
        this.email = email;
        this.nickname = nickname;
        this.exchangeStatus = exchangeStatus;
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

    public void addAuthAccount(AuthAccount authAccount) {
        this.authAccounts.add(authAccount);
        authAccount.assignUser(this);
    }

    public void removeAuthAccount(AuthAccount authAccount) {
        this.authAccounts.remove(authAccount);
        authAccount.unassignUser();
    }

    public void verifyEmail() {
        this.isVerified = true;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeExchangeStatus(ExchangeStatus exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    public void changeRole(UserRole role) {
        this.role = role;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public void softDelete() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public ExchangeStatus getExchangeStatus() {
        return exchangeStatus;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public boolean isActive() {
        return isActive;
    }

    public List<AuthAccount> getAuthAccounts() {
        return authAccounts;
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