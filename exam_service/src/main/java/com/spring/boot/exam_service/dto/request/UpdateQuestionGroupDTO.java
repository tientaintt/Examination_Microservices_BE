package com.spring.boot.exam_service.dto.request;


import com.spring.boot.exam_service.validate.ValidateUpdateQuestionGroup;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateUpdateQuestionGroup
public class UpdateQuestionGroupDTO {
    private String code;
    private String name;
    private String description;
}
