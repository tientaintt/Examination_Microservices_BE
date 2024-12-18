package com.spring.boot.exam_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class QuestionResponse {
    private Long id;
    private String questionId;
    private String content;
    private List<AnswerResponse> answers;
    private String questionType;
    private String imageUrl;
}
