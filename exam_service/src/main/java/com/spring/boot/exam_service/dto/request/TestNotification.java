package com.spring.boot.exam_service.dto.request;

import lombok.Builder;
import lombok.Data;


import java.util.List;
@Builder
@Data

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
