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
@Table(name = "subject")
@ToString
public class Subject extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;
    private static final String SUBJECT_ID = "id";
    private static final String SUBJECT_NAME = "subject_name";
    private static final String SUBJECT_CODE = "subject_code";
    private static final String DESCRIPTION = "description";
    private static final String IS_PRIVATE = "is_private";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = SUBJECT_ID, nullable = false)
    private Long id;

    @Column(name = SUBJECT_NAME)
    private String subjectName;

    @Column(name = SUBJECT_CODE)
    private String subjectCode;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = IS_PRIVATE)
    private Boolean isPrivate;

    @Column(name="managerId")
    private String userID;

    @OneToMany(
            mappedBy = "subject",
            cascade = CascadeType.ALL
    )
    private List<SubjectRegistration> subjectRegistrations;

    @OneToMany(
            mappedBy = "subject",
            cascade = CascadeType.ALL
    )
    private List<QuestionGroup> questionGroups;

    @OneToMany(
            mappedBy = "subject",
            cascade = CascadeType.ALL
    )
    private List<MultipleChoiceTest> multipleChoiceTests;
}