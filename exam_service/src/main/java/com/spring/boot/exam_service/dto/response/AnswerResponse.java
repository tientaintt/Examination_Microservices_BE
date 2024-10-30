package com.spring.boot.exam_service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AnswerResponse {
    private Long idAnswerQuestion;
    private Boolean isCorrect;
    private String answerContent;

}
