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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = "question_id", nullable = true, unique = true)
    private String questionId;

    @PostPersist
    public void generateQuestionId() {
        this.questionId = "question_" + this.id;
    }

    @Column(name = CONTENT)
    private String content;

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
