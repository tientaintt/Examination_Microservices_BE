package com.spring.boot.exam_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@Builder
@NoArgsConstructor
public class TestTrackingResponse {
    private Long id;

    private String studentId;

    private Long multipleChoiceTestId;

    private Long firstTimeAccess;

    private Long dueTime;

    public TestTrackingResponse(Long id, String studentId, Long multipleChoiceTestId, Long firstTimeAccess, Long dueTime) {
        this.id = id;
        this.studentId = studentId;
        this.multipleChoiceTestId = multipleChoiceTestId;
        this.firstTimeAccess = firstTimeAccess;
        this.dueTime = dueTime;
    }
}
