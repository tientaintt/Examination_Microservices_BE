package com.spring.boot.exam_service.dto.request;

import com.spring.boot.exam_service.validate.ValidateCreateQuestionGroup;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateCreateQuestionGroup
public class CreateQuestionGroupDTO {
    private String code;
    private String name;
    private Long classroomId;
    private String description;
}
