package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;



@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "classroom_registration")
public class ClassroomRegistration extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Classroom classRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserProfile userProfile;


}