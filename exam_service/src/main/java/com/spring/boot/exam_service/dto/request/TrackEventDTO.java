package com.spring.boot.exam_service.dto.request;



import com.spring.boot.exam_service.validate.ValidateAddToSubject;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TrackEventDTO {
    private String type;
    private Double x;
    private Double y;
    private String key;
    private String visibility;
    private Date timestamp;
}
