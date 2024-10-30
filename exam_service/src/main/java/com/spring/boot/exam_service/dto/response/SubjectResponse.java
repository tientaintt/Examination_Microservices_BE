package com.spring.boot.exam_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SubjectResponse {
    private Long id;
    private String subjectName;
    private String subjectCode;
    private Boolean isEnable;
    private Boolean isPrivate;
    private String description;
    private Long numberOfStudents;

    public SubjectResponse(Long id, String subjectName, String subjectCode, Boolean isEnable, Boolean isPrivate, String description, Long numberOfStudents) {
        this.id = id;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.isEnable = isEnable;
        this.isPrivate = isPrivate;
        this.description = description;
        this.numberOfStudents = numberOfStudents;
    }
}