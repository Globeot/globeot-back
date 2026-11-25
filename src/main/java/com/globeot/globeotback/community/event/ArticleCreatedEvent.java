package com.globeot.globeotback.community.event;

public record ArticleCreatedEvent(Long articleId, String type, String title, String authorNickname, String content) {
}
