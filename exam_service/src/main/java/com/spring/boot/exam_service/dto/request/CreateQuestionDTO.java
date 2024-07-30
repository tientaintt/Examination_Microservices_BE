package com.spring.boot.exam_service.dto.request;


import com.spring.boot.exam_service.validate.ValidateCreateQuestion;
import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateCreateQuestion
public class CreateQuestionDTO {
    private String content;
    private Answer firstAnswer;
    private Answer secondAnswer;
    private Answer thirdAnswer;
    private Answer fourthAnswer;
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
