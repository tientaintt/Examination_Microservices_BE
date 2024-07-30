package com.spring.boot.exam_service.dto.request;

import com.spring.boot.exam_service.validate.ValidateUpdateQuestion;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateUpdateQuestion
public class UpdateQuestionDTO {
    private String content;
    private CreateQuestionDTO.Answer firstAnswer;
    private CreateQuestionDTO.Answer secondAnswer;
    private CreateQuestionDTO.Answer thirdAnswer;
    private CreateQuestionDTO.Answer fourthAnswer;
}
