package com.spring.boot.exam_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
public class StudentScoreResponse {
    private Long id;
    private Long testId;
    private Double totalScore;
    private Instant submittedDate;
    private Boolean isLate;
    private String studentId;
    private String studentDisplayName;
    private String studentLoginName;
    private Double targetScore;

    public StudentScoreResponse(Long id, Long testId, Double totalScore, Instant submittedDate, Boolean isLate, String studentId, String studentDisplayName, String studentLoginName, Double targetScore) {
        this.id = id;
        this.testId = testId;
        this.totalScore = totalScore;
        this.submittedDate = submittedDate;
        this.isLate = isLate;
        this.studentId = studentId;
        this.studentDisplayName = studentDisplayName;
        this.studentLoginName = studentLoginName;
        this.targetScore = targetScore;
    }
}
