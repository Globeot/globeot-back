package com.globeot.globeotback.community.domain;

import com.globeot.globeotback.community.enums.Region;
import com.globeot.globeotback.community.enums.SaleStatus;
import com.globeot.globeotback.community.enums.Status;
import com.globeot.globeotback.community.enums.Type;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    private Status status;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private SaleStatus saleStatus;

    private Integer viewCount;

    private Integer reportCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}