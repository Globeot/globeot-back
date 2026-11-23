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

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // 사진 작가와 라이선스 표기. 예: "Daniel Schwen / CC BY-SA 2.5"
    private String imageCredit;

    // 사진 원본 페이지 (Wikimedia Commons 파일 페이지)
    @Column(length = 500)
    private String imageSourceUrl;

    @Column(columnDefinition = "json")
    private List<String> popularMajors;

    @Enumerated(EnumType.STRING)
    private Level travelAccessLevel;

    private String travelAccess;

    @Enumerated(EnumType.STRING)
    private Level monthlyCostLevel;

    private String monthlyCost;

    private Integer internationalStudentRatio;

    private String buddyProgram;

    private String officialSite;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}