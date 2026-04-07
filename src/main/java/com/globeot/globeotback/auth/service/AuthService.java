package com.globeot.globeotback.auth.service;

import com.globeot.globeotback.auth.domain.AuthAccount;
import com.globeot.globeotback.auth.domain.EmailVerification;
import com.globeot.globeotback.auth.dto.*;
import com.globeot.globeotback.auth.enums.AuthProvider;
import com.globeot.globeotback.auth.jwt.JwtProvider;
import com.globeot.globeotback.auth.repository.AuthAccountRepository;
import com.globeot.globeotback.auth.repository.EmailVerificationRepository;
import com.globeot.globeotback.global.exception.CustomException;
import com.globeot.globeotback.global.exception.ErrorCode;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthAccountRepository authAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;

    public AuthService(
            UserRepository userRepository,
            AuthAccountRepository authAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            EmailVerificationRepository emailVerificationRepository,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.authAccountRepository = authAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.emailVerificationRepository = emailVerificationRepository;
        this.emailService = emailService;
    }

    private void validateSchoolEmail(String email) {
        if (!(email.endsWith("@ewha.ac.kr") || email.endsWith("@ewhain.net"))) {
            throw new CustomException(ErrorCode.INVALID_SCHOOL_EMAIL);
        }
    }

    private void validateUserForSignup(String email) {
        String normalizedEmail = email.trim().toLowerCase();

        User existingUser = userRepository.findByEmail(normalizedEmail).orElse(null);

        if (existingUser != null) {
            if (existingUser.getDeletedAt() == null) {
                throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }

            if (existingUser.getDeletedAt()
                    .isAfter(LocalDateTime.now().minusDays(30))) {
                throw new CustomException(ErrorCode.USER_RECENTLY_DELETED);
            }
        }
    }

    private String generateOtp() {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    @Transactional
    public void sendOtp(String email) {
        validateSchoolEmail(email);
        validateUserForSignup(email);

        String otp = generateOtp();

        EmailVerification verification =
                emailVerificationRepository.findByEmail(email).orElse(null);

        if (verification != null) {

            if (verification.getSendCount() >= 5) {
                throw new CustomException(ErrorCode.OTP_SEND_LIMIT_EXCEEDED);
            }

            if (verification.getLastSentAt() != null &&
                    verification.getLastSentAt().isAfter(LocalDateTime.now().minusSeconds(60))) {
                throw new CustomException(ErrorCode.OTP_TOO_FAST);
            }

            verification.setOtp(otp);
            verification.setExpireTime(LocalDateTime.now().plusMinutes(5));
            verification.setSendCount(verification.getSendCount() + 1);
            verification.setLastSentAt(LocalDateTime.now());

        } else {
            verification = new EmailVerification(
                    email,
                    otp,
                    LocalDateTime.now().plusMinutes(5)
            );
            verification.setSendCount(1);
            verification.setLastSentAt(LocalDateTime.now());
        }

        emailVerificationRepository.save(verification);
        emailService.sendOtpMail(email, otp);
    }

    @Transactional(noRollbackFor = CustomException.class)
    public void verifyOtp(String email, String otp) {

        validateUserForSignup(email);

        EmailVerification verification =
                emailVerificationRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new CustomException(ErrorCode.OTP_NOT_REQUESTED));

        if (verification.getBlockedUntil() != null &&
                verification.getBlockedUntil().isAfter(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.OTP_BLOCKED);
        }

        if (verification.isExpired()) {
            throw new CustomException(ErrorCode.OTP_EXPIRED);
        }

        if (!verification.getOtp().equals(otp)) {

            verification.setVerifyFailCount(
                    verification.getVerifyFailCount() + 1
            );

            if (verification.getVerifyFailCount() >= 10) {
                verification.setBlockedUntil(LocalDateTime.now().plusHours(1));
            }

            emailVerificationRepository.save(verification);

            if (verification.getVerifyFailCount() == 3) {
                throw new CustomException(ErrorCode.OTP_FAIL_THREE_TIMES);
            }

            throw new CustomException(ErrorCode.OTP_MISMATCH);
        }

        verification.verify();
        verification.setVerifyFailCount(0);
        emailVerificationRepository.save(verification);
    }

    @Transactional
    public SignupResponseDto signup(SignupRequestDto request) {

        validateUserForSignup(request.getEmail());

        EmailVerification verification =
                emailVerificationRepository.findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new CustomException(ErrorCode.EMAIL_NOT_VERIFIED));

        if (!verification.isVerified()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        validateSchoolEmail(request.getEmail());
        validateUserForSignup(request.getEmail());

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        User user = new User(
                request.getEmail(),
                request.getNickname(),
                request.getExchangeStatus()
        );
        user.verifyEmail();

        String passwordHash = passwordEncoder.encode(request.getPassword());

        AuthAccount authAccount =
                new AuthAccount(AuthProvider.LOCAL, request.getEmail(), passwordHash, user);

        user.addAuthAccount(authAccount);
        userRepository.save(user);

        String token = jwtProvider.createToken(user.getId());
        emailVerificationRepository.delete(verification);

        return new SignupResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getExchangeStatus(),
                token
        );
    }

    @Transactional(noRollbackFor = CustomException.class)
    public LoginResponseDto login(LoginRequestDto request) {

        AuthAccount authAccount = authAccountRepository
                .findByProviderAndProviderUserId(
                        AuthProvider.LOCAL,
                        request.getEmail()
                )
                .orElseThrow(() ->
                        new CustomException(ErrorCode.LOGIN_FAILED));

        User user = authAccount.getUser();

        if (user.getDeletedAt() != null || !user.isActive()) {
            throw new CustomException(ErrorCode.USER_DELETED);
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                authAccount.getPasswordHash()
        )) {

            authAccount.setLoginFailCount(
                    authAccount.getLoginFailCount() + 1
            );

            if (authAccount.getLoginFailCount() >= 5) {

                String tempPassword = generateTempPassword();

                authAccount.setPasswordHash(
                        passwordEncoder.encode(tempPassword)
                );

                authAccount.setLoginFailCount(0);
                authAccountRepository.save(authAccount);

                emailService.sendPasswordResetMail(
                        authAccount.getProviderUserId(),
                        tempPassword
                );

                throw new CustomException(ErrorCode.PASSWORD_RESET);
            }

            authAccountRepository.save(authAccount);

            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        authAccount.setLoginFailCount(0);
        authAccount.setLastLoginAt(LocalDateTime.now());
        authAccountRepository.save(authAccount);

        String token = jwtProvider.createToken(user.getId());

        return new LoginResponseDto(user.getId(), token);
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}