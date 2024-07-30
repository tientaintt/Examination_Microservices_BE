package com.spring.boot.exam_service.dto.request;



import com.spring.boot.exam_service.validate.ValidateAddToClassroom;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateAddToClassroom
public class AddToClassroomDTO {
    private Long classroomId;
    private String studentId;
}
