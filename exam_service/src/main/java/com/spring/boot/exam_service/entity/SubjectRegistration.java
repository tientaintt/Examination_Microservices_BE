package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;



@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "subject_registration")
public class SubjectRegistration extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;

    private String userID;

}