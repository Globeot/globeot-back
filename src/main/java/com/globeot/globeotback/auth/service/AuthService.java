package com.globeot.globeotback.auth.service;

import com.globeot.globeotback.auth.domain.AuthAccount;
import com.globeot.globeotback.auth.domain.EmailVerification;
import com.globeot.globeotback.auth.dto.LoginRequestDto;
import com.globeot.globeotback.auth.dto.LoginResponseDto;
import com.globeot.globeotback.auth.dto.SignupRequestDto;
import com.globeot.globeotback.auth.dto.SignupResponseDto;
import com.globeot.globeotback.auth.enums.AuthProvider;
import com.globeot.globeotback.auth.jwt.JwtProvider;
import com.globeot.globeotback.auth.repository.AuthAccountRepository;
import com.globeot.globeotback.auth.repository.EmailVerificationRepository;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

        if (!(email.endsWith("@ewha.ac.kr")
                || email.endsWith("@ewhain.net"))) {
            throw new IllegalArgumentException("학교 메일이 아닙니다.");
        }
    }

    private String generateOtp() {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }

    @Transactional
    public void sendOtp(String email) {

        validateSchoolEmail(email);

        String otp = generateOtp();

        emailVerificationRepository.findByEmail(email).ifPresent(existing -> {
            if (!existing.isExpired()) {
                throw new IllegalArgumentException("이메일로 이미 인증번호가 발송되었습니다. 5분 후 다시 시도해주세요.");
            }
            // 만료되었으면 삭제하고 새로 발송
            emailVerificationRepository.delete(existing);
        });

        EmailVerification verification =
                new EmailVerification(
                        email,
                        otp,
                        LocalDateTime.now().plusMinutes(5)
                );

        emailVerificationRepository.save(verification);

        emailService.sendOtpMail(email, otp);
    }

    @Transactional
    public void verifyOtp(String email, String otp) {

        EmailVerification verification =
                emailVerificationRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException("인증번호 요청이 없습니다."));

        if (verification.isExpired()) {
            throw new IllegalArgumentException("인증 시간이 만료되었습니다.");
        }

        if (!verification.getOtp().equals(otp)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        verification.verify();
        emailVerificationRepository.save(verification);
    }

    @Transactional
    public SignupResponseDto signup(SignupRequestDto request) {
        try{
            System.out.println("Signup 시작: " + request.getEmail());
            EmailVerification verification =
                    emailVerificationRepository.findByEmail(request.getEmail())
                            .orElseThrow(() ->
                                    new IllegalArgumentException("이메일 인증이 필요합니다."));

            if (!verification.isVerified()) {
                throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
            }

            validateSchoolEmail(request.getEmail());

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 가입된 이메일입니다.");
            }

            if (userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
            }

            // User 생성
            User user = new User(
                    request.getEmail(),
                    request.getNickname(),
                    request.getExchangeStatus()
            );
            user.verifyEmail();

            // AuthAccount 생성 & User에 추가
            String passwordHash = passwordEncoder.encode(request.getPassword());
            AuthAccount authAccount = new AuthAccount(AuthProvider.LOCAL, request.getEmail(), passwordHash, user);
            user.addAuthAccount(authAccount);

            // User 저장 (Cascade.ALL)
            userRepository.save(user);

            System.out.println("User: " + user.getEmail() + ", AuthAccounts size: " + user.getAuthAccounts().size());
            AuthAccount account = user.getAuthAccounts().get(0);
            System.out.println("AuthAccount provider: " + account.getProvider() + ", providerUserId: " + account.getProviderUserId());

            // JWT 발급
            String token = jwtProvider.createToken(user.getId());

            return new SignupResponseDto(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname(),
                    user.getExchangeStatus(),
                    token
            );

        } catch (Exception e) {
            e.printStackTrace(); // 서버 콘솔에 전체 오류 확인
            throw e; // 다시 던져서 rollback 유지
        }



    }

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {

        AuthAccount authAccount =
                authAccountRepository
                        .findByProviderAndProviderUserId(
                                AuthProvider.LOCAL,
                                request.getEmail()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(
                request.getPassword(),
                authAccount.getPasswordHash()
        )) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        User user = authAccount.getUser();

        String token = jwtProvider.createToken(user.getId());

        return new LoginResponseDto(user.getId(), token);
    }
}