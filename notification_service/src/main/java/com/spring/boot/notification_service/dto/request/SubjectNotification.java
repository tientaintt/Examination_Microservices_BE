package com.spring.boot.notification_service.dto.request;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectNotification {
    private Long id;
    private String subjectName;
    private String subjectCode;
}
