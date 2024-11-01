package com.spring.boot.exam_service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportTestByMonthResponse {
    Integer month;
    Long numberOfTests;
    public ReportTestByMonthResponse(Integer month, Long numberOfTests) {
        this.month = month;
        this.numberOfTests = numberOfTests;
    }
}
