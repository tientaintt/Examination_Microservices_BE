package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;



@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "submitted_question")
public class SubmittedQuestion {

    private static final long serialVersionUID = 1L;
    private static final String ID = "id";
    private static final String QUESTION_ID = "questionId";
    private static final String CONTENT = "content";
    private static final String FIRST_ANSWER = "first_answer";
    private static final String SECOND_ANSWER = "second_answer";
    private static final String THIRD_ANSWER = "third_answer";
    private static final String FOURTH_ANSWER = "fourth_answer";
    private static final String CORRECT_ANSWER = "correct_answer";
    private static final String SUBMITTED_ANSWER = "submitted_answer";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

//    @Column(name = QUESTION_ID)
//    private Long questionId;

//    @Column(name = CONTENT)
//    private String content;
//
//    @Column(name = FIRST_ANSWER)
//    private String firstAnswer;
//
//    @Column(name = SECOND_ANSWER)
//    private String secondAnswer;
//
//    @Column(name = THIRD_ANSWER)
//    private String thirdAnswer;
//
//    @Column(name = FOURTH_ANSWER)
//    private String fourthAnswer;
//
//    @Column(name = CORRECT_ANSWER)
//    private String correctAnswer;
//
    @Column(name = SUBMITTED_ANSWER)
    private String submittedAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Score score;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}
