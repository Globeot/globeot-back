package com.globeot.globeotback.community.domain;

import com.globeot.globeotback.community.enums.Reason;
import com.globeot.globeotback.community.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private com.globeot.globeotback.user.domain.User user;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private com.globeot.globeotback.community.domain.Article article;

    @Enumerated(EnumType.STRING)
    private Reason reason;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private LocalDateTime createdAt;

}