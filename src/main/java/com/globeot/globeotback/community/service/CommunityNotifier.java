package com.globeot.globeotback.community.service;

import com.globeot.globeotback.community.event.ArticleCreatedEvent;
import com.globeot.globeotback.community.event.CommentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 커뮤니티에 새 글·댓글이 등록되면 운영진에게 알림 메일을 보낸다.
 * 저장(커밋)이 끝난 뒤 비동기로 보내므로, 메일이 실패해도 글·댓글 작성에는 영향이 없다.
 * 받는 주소는 app.notify.community-email (비우면 발송 안 함).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommunityNotifier {

    private static final int PREVIEW_LENGTH = 300;

    private final JavaMailSender mailSender;

    @Value("${app.notify.community-email:}")
    private String communityEmail;

    @Value("${app.notify.front-url:https://globeot.vercel.app}")
    private String frontUrl;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onArticleCreated(ArticleCreatedEvent e) {
        send(
                "[글로벗] 새 게시글: " + e.title(),
                "커뮤니티에 새 게시글이 올라왔어요.\n\n"
                        + "- 종류: " + e.type() + "\n"
                        + "- 작성자: " + e.authorNickname() + "\n"
                        + "- 제목: " + e.title() + "\n\n"
                        + preview(e.content()) + "\n\n"
                        + "바로가기: " + frontUrl + "/community/" + e.articleId()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentCreated(CommentCreatedEvent e) {
        String kind = e.reply() ? "답글" : "댓글";
        send(
                "[글로벗] 새 " + kind + ": " + e.articleTitle(),
                "게시글에 새 " + kind + "이 달렸어요.\n\n"
                        + "- 게시글: " + e.articleTitle() + "\n"
                        + "- 작성자: " + e.authorNickname() + "\n\n"
                        + preview(e.content()) + "\n\n"
                        + "바로가기: " + frontUrl + "/community/" + e.articleId()
        );
    }

    private void send(String subject, String text) {
        if (communityEmail == null || communityEmail.isBlank()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(communityEmail.split("\\s*,\\s*"));
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception ex) {
            log.warn("커뮤니티 알림 메일 발송 실패: {}", ex.getMessage());
        }
    }

    private String preview(String content) {
        if (content == null) {
            return "";
        }
        String trimmed = content.strip();
        return trimmed.length() > PREVIEW_LENGTH ? trimmed.substring(0, PREVIEW_LENGTH) + "…" : trimmed;
    }
}
