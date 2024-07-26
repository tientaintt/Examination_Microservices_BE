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
@Table(name = "question")
public class Question extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;
    private static final String ID = "id";
    private static final String CONTENT = "content";
    private static final String FIRST_ANSWER = "first_answer";
    private static final String SECOND_ANSWER = "second_answer";
    private static final String THIRD_ANSWER = "third_answer";
    private static final String FOURTH_ANSWER = "fourth_answer";
    private static final String CORRECT_ANSWER = "correct_answer";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = CONTENT)
    private String content;

    @Column(name = FIRST_ANSWER)
    private String firstAnswer;

    @Column(name = SECOND_ANSWER)
    private String secondAnswer;

    @Column(name = THIRD_ANSWER)
    private String thirdAnswer;

    @Column(name = FOURTH_ANSWER)
    private String fourthAnswer;

    @Column(name = CORRECT_ANSWER)
    private String correctAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuestionGroup questionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuestionType questionType;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL
    )
    private List<TestQuestion> testQuestions;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL
    )
    private List<AnswerQuestion> answerQuestions;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL
    )
    private List<SubmittedQuestion> submittedQuestions;
}
