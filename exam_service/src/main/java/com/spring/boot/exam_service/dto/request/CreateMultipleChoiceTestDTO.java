package com.spring.boot.exam_service.dto.request;


import com.spring.boot.exam_service.validate.ValidateCreateMultipleChoiceTest;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateCreateMultipleChoiceTest
public class CreateMultipleChoiceTestDTO {
    private String testName;
    // milliseconds since January 1, 1970
    private Long startDate;
    // milliseconds since January 1, 1970
    private Long endDate;
    // minutes
    private Long testingTime;

    private String description;

    private Long classroomId;

    private Double targetScore;

    private List<Long> questionIds;

    private List<Questions> randomQuestions;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Questions {
        private Long questionGroupId;
        private Long numberOfQuestion;
    }
}
