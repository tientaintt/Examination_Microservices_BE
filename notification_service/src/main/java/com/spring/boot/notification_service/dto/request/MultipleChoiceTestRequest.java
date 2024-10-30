package com.spring.boot.notification_service.dto.request;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data

public class MultipleChoiceTestRequest {

    private Long id;

    private String testName;

    private String description;


    private Long startDate; // milliseconds since January 1, 1970


    private Long endDate; // milliseconds since January 1, 1970


    private Long testingTime;

    private Long classId;
    private String className;
    private List<String> registerUserEmails;
}
