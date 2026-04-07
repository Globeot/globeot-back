package com.globeot.globeotback.community.dto;

import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.Region;
import com.globeot.globeotback.community.enums.Type;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import java.util.List;

@Getter
public class ArticleUpdateDto {
    @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
    private String title;
    private String content;
    private Region region;
    private Type type;
    private ExchangeStatus exchangeStatus;
    private String topic;
    private Long schoolId;
    private List<String> imageUrls;
    private ArticleStatus articleStatus;
}
