package com.globeot.globeotback.community.domain;

import com.globeot.globeotback.community.enums.Region;
import com.globeot.globeotback.community.enums.ArticleStatus;
import com.globeot.globeotback.community.enums.Type;
import com.globeot.globeotback.user.enums.ExchangeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "articles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private com.globeot.globeotback.user.domain.User author;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private com.globeot.globeotback.school.domain.School school;

    @Enumerated(EnumType.STRING)
    private ExchangeStatus exchangeStatus;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String topic;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING)
    private ArticleStatus articleStatus;

    private Integer viewCount;

    private Integer reportCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}