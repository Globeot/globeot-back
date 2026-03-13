package com.globeot.globeotback.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;

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
    @Column(nullable = false)
    private String otp;

    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime;

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