package com.globeot.globeotback.community.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.globeot.globeotback.user.domain.User;
import com.globeot.globeotback.school.domain.School;

@Entity
@Table(name = "favorites")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    private LocalDateTime createdAt;
}