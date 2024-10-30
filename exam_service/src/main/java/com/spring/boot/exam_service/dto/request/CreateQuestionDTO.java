package com.spring.boot.exam_service.dto.request;


import com.spring.boot.exam_service.validate.ValidateCreateQuestion;
import lombok.*;

import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateCreateQuestion
public class CreateQuestionDTO {
    private String content;
    private List<Answer> answers;
    private String questionType;

    private Long questionGroupId;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {
        private String answerContent;
        private Boolean isCorrect;
    }
}
