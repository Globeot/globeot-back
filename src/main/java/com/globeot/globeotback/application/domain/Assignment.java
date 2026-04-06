package com.globeot.globeotback.application.domain;

import com.globeot.globeotback.school.domain.School;
import com.globeot.globeotback.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    @Column(name = "converted_score", nullable = false)
    private Double convertedScore;
}