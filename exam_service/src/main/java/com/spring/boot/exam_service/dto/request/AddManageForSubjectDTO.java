package com.spring.boot.exam_service.dto.request;

import com.spring.boot.exam_service.validate.ValidateAddManageForSubject;
import com.spring.boot.exam_service.validate.ValidateAddToSubject;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateAddManageForSubject
public class AddManageForSubjectDTO {
    private Long subjectId;
    private String teacherId;
}
