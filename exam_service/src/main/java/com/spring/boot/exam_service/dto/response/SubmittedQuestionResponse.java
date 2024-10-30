package com.spring.boot.exam_service.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmittedQuestionResponse {
    private Long id;
    private Long questionId;
    private String questionType;
    private String content;

    private List<AnswerResponse> answers;
    private String correctAnswer;
    private String submittedAnswer;


}
