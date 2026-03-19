package com.globeot.globeotback.community.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.globeot.globeotback.user.domain.User;

@Entity
@Table(name = "scraps")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    private LocalDateTime createdAt;
}