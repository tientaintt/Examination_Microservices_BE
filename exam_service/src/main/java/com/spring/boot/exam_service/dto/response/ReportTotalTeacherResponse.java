package com.spring.boot.exam_service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ReportTotalTeacherResponse {
    int totalTests;
    int totalSubjects;
    int totalStudents;
    int totalQuestions;
}
