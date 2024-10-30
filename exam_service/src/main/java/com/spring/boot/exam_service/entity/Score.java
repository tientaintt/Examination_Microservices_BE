package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "score")
public class Score extends AbstractAuditingEntity {

    private static final String TOTAL_SCORE = "total_core";
    private static final String TOTAL_CORRECT = "total_correct";
    private static final String SUBMITTED_DATE = "submitted_date";
    private static final String MULTIPLE_CHOICE_TEST_ID = "multiple_choice_test_id";
    private static final String TARGET_SCORE = "target_score";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = TOTAL_SCORE)
    private double totalCore;

    @Column(name = TOTAL_CORRECT)
    private Long totalCorrect;

    @Column(name = SUBMITTED_DATE)
    private Long submittedDate;

    private boolean isLate;

    @Column(name = TARGET_SCORE)
    private Double targetScore;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = MULTIPLE_CHOICE_TEST_ID, referencedColumnName = "id")
    private MultipleChoiceTest multipleChoiceTest;


    private String userID;

    @OneToMany(
            mappedBy = "score",
            cascade = CascadeType.ALL
    )
    private List<SubmittedQuestion> submittedQuestions;
}
