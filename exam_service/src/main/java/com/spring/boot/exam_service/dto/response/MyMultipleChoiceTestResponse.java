package com.spring.boot.exam_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
public class MyMultipleChoiceTestResponse {
    private Long id;
    private String createdBy;
    private Long startDate;
    private Long endDate;
    private String testName;
    private String testDescription;
    private Long testingTime;
    private Long classroomId;
    private String className;
    private String classCode;
    private String classDescription;
    private Boolean isSubmitted;

    public MyMultipleChoiceTestResponse(Long id, String createdBy, Long startDate, Long endDate, String testName, String testDescription, Long testingTime, Long classroomId, String className, String classCode, String classDescription, Boolean isSubmitted) {
        this.id = id;
        this.createdBy = createdBy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.testName = testName;
        this.testDescription = testDescription;
        this.testingTime = testingTime;
        this.classroomId = classroomId;
        this.className = className;
        this.classCode = classCode;
        this.classDescription = classDescription;
        this.isSubmitted = isSubmitted;
    }
}
