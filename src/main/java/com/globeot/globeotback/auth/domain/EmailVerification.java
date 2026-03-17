package com.globeot.globeotback.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Getter
    @Setter
    @Column(nullable = false)
    private String otp;

    @Getter
    @Setter
    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime;

    @Getter
    @Setter
    @Column(nullable = false)
    private int verifyFailCount=0;

    @Getter
    @Setter
    @Column(nullable = false)
    private int sendCount=0;

    @Getter
    @Setter
    private LocalDateTime lastSentAt;

    @Getter
    @Setter
    private LocalDateTime blockedUntil;

    @Getter
    @Column(nullable = false)
    private boolean verified = false;

    public void verify() {
        this.verified = true;
    }

    public boolean isExpired() {
        return expireTime.isBefore(LocalDateTime.now());
    }

    protected EmailVerification() {

    }

    public EmailVerification(String email, String otp, LocalDateTime expireTime) {
        this.email = email;
        this.otp = otp;
        this.expireTime = expireTime;
    }
}