package com.spring.boot.notification_service.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

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
}
