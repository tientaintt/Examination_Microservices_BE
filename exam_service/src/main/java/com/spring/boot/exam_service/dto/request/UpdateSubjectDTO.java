package com.spring.boot.exam_service.dto.request;

import com.spring.boot.exam_service.validate.ValidateUpdateClassroom;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateUpdateClassroom
public class UpdateSubjectDTO {
    private String subjectCode;
    private String subjectName;
    private String description;
    private Boolean isPrivate;
}