package com.spring.boot.exam_service.dto.request;


import com.spring.boot.exam_service.validate.ValidateCreateClassroom;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateCreateClassroom
public class CreateClassroomDTO {
    private String classCode;
    private String className;
    private String description;
    private Boolean isPrivate;
}