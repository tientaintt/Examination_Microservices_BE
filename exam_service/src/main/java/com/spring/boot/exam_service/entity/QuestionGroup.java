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
@Table(name = "question_group")
public class QuestionGroup extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;
    private static final String ID = "id";
    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID, nullable = false)
    private Long id;

    @Column(name = CODE)
    private String code;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = NAME)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;

    @OneToMany(
            mappedBy = "questionGroup",
            cascade = CascadeType.ALL
    )
    private List<Question> questions;
}
