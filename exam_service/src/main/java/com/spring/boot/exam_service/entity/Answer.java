package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name="answer")
public class Answer extends AbstractAuditingEntity{
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String answer;

    @OneToMany (
            mappedBy = "answer",
            cascade = CascadeType.ALL
    )
    List<AnswerQuestion> answerQuestions;

}
