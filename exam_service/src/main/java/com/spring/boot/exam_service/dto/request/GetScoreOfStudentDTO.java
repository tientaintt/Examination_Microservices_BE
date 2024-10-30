package com.spring.boot.exam_service.dto.request;


import com.spring.boot.exam_service.validate.ValidateGetScoreOfStudent;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateGetScoreOfStudent
public class GetScoreOfStudentDTO {
    private String studentId;
    private Long multipleChoiceTestId;
}
