package com.spring.boot.notification_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class TestNotification {
    private Long id;

    private String testName;

    private String description;


    private Long startDate; // milliseconds since January 1, 1970


    private Long endDate; // milliseconds since January 1, 1970


    private Long testingTime;

    private Long subjectId;
    private String subjectName;
    private List<String> registerUserEmails;
}
