package com.globeot.globeotback.auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpMail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Globeot] 이메일 인증 코드");
        message.setText("인증 코드: " + otp + "\n5분 내 입력해주세요.");

        mailSender.send(message);
    }


    public void sendPasswordResetMail(String email, String tempPassword) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("[Globeot] 비밀번호 초기화 안내");

        mail.setText(
                "로그인 시도 5회 실패로 비밀번호가 초기화되었습니다.\n\n"
                        + "임시 비밀번호: " + tempPassword + "\n\n"
                        + "로그인 후 반드시 비밀번호를 변경해주세요."
        );

        mailSender.send(mail);
    }
}

