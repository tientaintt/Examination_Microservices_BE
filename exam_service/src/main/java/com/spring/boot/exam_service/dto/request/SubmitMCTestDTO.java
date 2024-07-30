package com.spring.boot.exam_service.dto.request;


import com.spring.boot.exam_service.validate.ValidateSubmitMCTest;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateSubmitMCTest
public class SubmitMCTestDTO {
    private Long multipleChoiceTestId;
    private List<SubmittedAnswer> submittedAnswers;
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmittedAnswer {
        private Long questionId;
        private String answer;

        public Long getQuestionId() {
            if(Objects.isNull(this.questionId)){
                this.questionId = 0L;
            }
            return questionId;
        }

        public String getAnswer() {
            if(Objects.isNull(this.answer)){
                this.answer = "";
            }
            return answer;
        }
    }
}
