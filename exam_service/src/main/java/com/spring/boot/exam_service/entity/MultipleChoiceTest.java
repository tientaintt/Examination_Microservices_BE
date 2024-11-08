package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "multiple_choice_test")
public class  MultipleChoiceTest extends AbstractAuditingEntity {
    private static final long serialVersionUID = 1L;
    private static final String TEST_ID = "id";
    private static final String TEST_NAME = "test_name";
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";
    private static final String TESTING_TIME = "testing_time";
    private static final String DESCRIPTION = "description";
    private static final String TARGET_SCORE = "target_score";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = TEST_ID, nullable = false)
    private Long id;

    @Column(name = TEST_NAME)
    private String testName;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = START_DATE)
    private Long startDate; // milliseconds since January 1, 1970

    @Column(name = END_DATE)
    private Long endDate; // milliseconds since January 1, 1970

    @Column(name = TESTING_TIME)
    private Long testingTime; // minutes

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;

    @Column(name = TARGET_SCORE)
    private Double targetScore;

    @OneToMany(
            mappedBy = "multipleChoiceTest",
            cascade = CascadeType.ALL
    )
    private List<TestQuestion> testQuestions;

    @OneToOne(mappedBy = "multipleChoiceTest")
    private Score score;

    @OneToMany(
            mappedBy = "multipleChoiceTest",
            cascade = CascadeType.ALL
    )
    private List<TestTracking> doTestHistories;
}
