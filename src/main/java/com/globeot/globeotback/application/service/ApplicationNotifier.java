package com.globeot.globeotback.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 지원서가 제출되면 운영진에게 알림 메일을 보낸다.
 * 성적표 이미지·실명·이메일·점수 등 개인정보는 메일에 넣지 않는다.
 * 비동기로 보내므로 메일 발송이 실패해도 지원서 제출에는 영향이 없다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationNotifier {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JavaMailSender mailSender;

    @Value("${app.notify.admin-email:}")
    private String adminEmail;

    @Async
    public void notifyNewApplication(String semester, String firstChoiceSchool, long pendingCount) {
        if (adminEmail == null || adminEmail.isBlank()) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail.split("\\s*,\\s*"));
            message.setSubject("[글로벗] 새 지원서 제출 (승인 대기 " + pendingCount + "건)");
            message.setText(
                    "새 지원서가 제출됐어요.\n\n"
                            + "- 지원 학기: " + semester + "\n"
                            + "- 1지망: " + (firstChoiceSchool != null ? firstChoiceSchool : "-") + "\n"
                            + "- 제출 시각: " + ZonedDateTime.now(KST).format(TIME_FORMAT) + " (KST)\n"
                            + "- 현재 승인 대기: " + pendingCount + "건\n\n"
                            + "관리자 화면에서 성적표를 확인하고 승인해 주세요."
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("지원서 제출 알림 메일 발송 실패: {}", e.getMessage());
        }
    }
}
