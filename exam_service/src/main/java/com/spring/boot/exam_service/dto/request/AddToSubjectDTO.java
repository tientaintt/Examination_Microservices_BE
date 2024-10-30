package com.spring.boot.exam_service.dto.request;



import com.spring.boot.exam_service.validate.ValidateAddToSubject;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateAddToSubject
public class AddToSubjectDTO {
    private Long subjectId;
    private String studentId;
}
