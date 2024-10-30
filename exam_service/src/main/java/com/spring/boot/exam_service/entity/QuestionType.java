package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "question_type")
public class QuestionType  extends  AbstractAuditingEntity{
    static final long serialVersionUID = 1L;
    static final String ID = "id";
    static final String TYPE_QUESTION = "type_question";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    Long id;

    @Column(name = TYPE_QUESTION)
    String typeQuestion;

    @OneToMany(
            mappedBy = "questionType",
            cascade = CascadeType.ALL
    )
    private List<Question> questions;
}
