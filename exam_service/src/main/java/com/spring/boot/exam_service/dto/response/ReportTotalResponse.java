package com.spring.boot.exam_service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportTotalResponse {
    int totalTests;
    int totalSubjects;
    int totalStudent;
}
