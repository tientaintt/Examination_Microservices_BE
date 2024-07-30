package com.spring.boot.exam_service.dto.response;

import lombok.*;
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmittedQuestionResponse {
    private Long id;
    private Long questionId;
    private String content;
    private String firstAnswer;
    private String secondAnswer;
    private String thirdAnswer;
    private String fourthAnswer;
    private String correctAnswer;
    private String submittedAnswer;
}
