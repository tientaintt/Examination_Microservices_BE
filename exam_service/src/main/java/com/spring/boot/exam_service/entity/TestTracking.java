package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;



@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "test_tracking")
public class TestTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    private MultipleChoiceTest multipleChoiceTest;

    private Long firstTimeAccess;

    private Long dueTime;
}
