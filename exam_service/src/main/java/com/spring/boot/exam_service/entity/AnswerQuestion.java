package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name="answer_question")
public class AnswerQuestion extends AbstractAuditingEntity {
    static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    Answer answer;
    @Column
    Boolean isCorrect;
}
