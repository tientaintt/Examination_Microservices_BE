package com.spring.boot.exam_service.dto.request;



import com.spring.boot.exam_service.validate.ValidateCreateSubject;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateCreateSubject
public class CreateSubjectDTO {
    private String subjectCode;
    private String subjectName;
    private String description;
    private Boolean isPrivate;
}