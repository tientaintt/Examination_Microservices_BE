package com.spring.boot.exam_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
public class MyScoreResponse {
    private Long id;
    private Double totalScore;
    private Boolean isLate;
    private Long submittedDate;
    private Long testId;
    private String testName;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Double targetScore;

    public MyScoreResponse(Long id, Double totalScore, Boolean isLate, Long submittedDate, Long testId, String testName, Long subjectId, String subjectName, String subjectCode, Double targetScore) {
        this.id = id;
        this.totalScore = totalScore;
        this.isLate = isLate;
        this.submittedDate = submittedDate;
        this.testId = testId;
        this.testName = testName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.targetScore = targetScore;
    }
}
