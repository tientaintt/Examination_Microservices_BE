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
@Table(name = "class_room")
@ToString
public class Classroom extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;
    private static final String CLASS_ID = "id";
    private static final String CLASS_NAME = "class_name";
    private static final String CLASS_CODE = "class_code";
    private static final String DESCRIPTION = "description";
    private static final String IS_PRIVATE = "is_private";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = CLASS_ID, nullable = false)
    private Long id;

    @Column(name = CLASS_NAME)
    private String className;

    @Column(name = CLASS_CODE)
    private String classCode;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = IS_PRIVATE)
    private Boolean isPrivate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="managerId")
    private UserProfile userProfile;

    @OneToMany(
            mappedBy = "classRoom",
            cascade = CascadeType.ALL
    )
    private List<ClassroomRegistration> ClassroomRegistrations;

    @OneToMany(
            mappedBy = "classRoom",
            cascade = CascadeType.ALL
    )
    private List<QuestionGroup> questionGroups;

    @OneToMany(
            mappedBy = "classRoom",
            cascade = CascadeType.ALL
    )
    private List<MultipleChoiceTest> multipleChoiceTests;
}