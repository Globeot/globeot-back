package com.globeot.globeotback.community.event;

public record CommentCreatedEvent(Long articleId, String articleTitle, String authorNickname, String content, boolean reply) {
}
