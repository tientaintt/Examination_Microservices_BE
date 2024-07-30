package com.spring.boot.exam_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class QuestionGroupResponse {
    private Long id;
    private String name;
    private String code;
    private Boolean isEnable;
    private Long totalQuestion;
    private String description;
}
