package com.globeot.globeotback.school.domain;

import com.globeot.globeotback.school.enums.Level;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "schools")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String city;

    private String country;

    private String imageUrl;

    @Column(columnDefinition = "json")
    private List<String> popularMajors;

    @Enumerated(EnumType.STRING)
    private Level travelAccessLevel;

    private String travelAccess;

    @Enumerated(EnumType.STRING)
    private Level monthlyCostLevel;

    private String monthlyCost;

    private int internationalStudentRatio;

    private String buddyProgram;

    private String officialSite;

    private Double avgScore;

    private Double maxScore;

    private Double minScore;

    @Column(columnDefinition = "JSON")
    private String assignments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}