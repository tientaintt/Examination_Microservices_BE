package com.spring.boot.exam_service.dto.request;


import com.spring.boot.exam_service.validate.ValidateUpdateMultipleChoiceTest;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateUpdateMultipleChoiceTest
public class UpdateMultipleChoiceTestDTO {
    private String testName;
    private String description;
    // milliseconds since January 1, 1970
    private Long startDate;
    // milliseconds since January 1, 1970
    private Long endDate;
    // minutes
    private Long testingTime;

    private Double targetScore;
}
