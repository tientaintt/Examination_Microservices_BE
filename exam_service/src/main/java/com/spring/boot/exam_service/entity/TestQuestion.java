package com.spring.boot.exam_service.entity;

import jakarta.persistence.*;
import lombok.*;



@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "test_question")
public class TestQuestion extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MultipleChoiceTest multipleChoiceTest;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}
