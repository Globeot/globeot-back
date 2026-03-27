package com.globeot.globeotback.application.domain;

import com.globeot.globeotback.application.enums.EnglishTestType;
import com.globeot.globeotback.application.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private com.globeot.globeotback.user.domain.User user;

    @Enumerated(EnumType.STRING)
    private EnglishTestType englishTestType;

    private Double gpa;
    private Double convertedScore;

    @Column(columnDefinition = "JSON")
    private String englishScore; // [{"reading": 90, "listening": 90, "speaking": 90, "writing": 90}]

    private String gpaImageUrl;
    private String englishTestImageUrl;

    private String semester;

    @Column(columnDefinition = "JSON")
    private String schools; // [{"priority":1, "school_id": 1, "school_name":ㅇㅇ대학교}, ...]

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

}