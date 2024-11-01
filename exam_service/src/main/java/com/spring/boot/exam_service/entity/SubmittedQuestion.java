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

    private static final String CORRECT_ANSWER = "correct_answer";
    private static final String SUBMITTED_ANSWER = "submitted_answer";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = "submitted_answer", length = 500)
    private String submittedAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Score score;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}
