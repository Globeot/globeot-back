package com.globeot.globeotback.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.Region;
import com.globeot.globeotback.community.enums.Type;
import java.util.List;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ArticleDetailDto {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorNickname;
    private ExchangeStatus exchangeStatus;
    private Region region;
    private Type type;
    private Integer viewCount;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String topic;
    private Long schoolId;
    private String schoolName;
    private List<String> imageUrls;
    private ArticleStatus articleStatus;

    @JsonProperty("isAuthor")
    private boolean isAuthor;

    @JsonProperty("isScrapped")
    private boolean isScrapped;

    @JsonProperty("isBlinded")
    private boolean isBlinded;
}
