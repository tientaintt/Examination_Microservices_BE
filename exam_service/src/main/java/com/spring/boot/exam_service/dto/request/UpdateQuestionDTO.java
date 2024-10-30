package com.spring.boot.exam_service.dto.request;

import com.spring.boot.exam_service.validate.ValidateUpdateQuestion;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateUpdateQuestion
public class UpdateQuestionDTO {
    private String content;
    private List<AnswerUpdate> answers;
    private String questionType;
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerUpdate {
        private Long idAnswerQuestion;
        private String answerContent;
        private Boolean isCorrect;
    }
}
