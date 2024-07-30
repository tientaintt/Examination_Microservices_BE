package com.spring.boot.exam_service.dto.request;

import com.spring.boot.exam_service.validate.ValidateUpdateClassroom;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateUpdateClassroom
public class UpdateClassroomDTO {
    private String classCode;
    private String className;
    private String description;
    private Boolean isPrivate;
}