package com.globeot.globeotback.community.dto;

import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.Region;
import com.globeot.globeotback.community.enums.Type;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleListDto {
    private Long id;
    private String title;
    private String authorNickname;
    private ExchangeStatus exchangeStatus;
    private Region region;
    private Type type;
    private LocalDateTime createdAt;
    private Long commentCount;
    private String topic;
    private ArticleStatus articleStatus;
}
