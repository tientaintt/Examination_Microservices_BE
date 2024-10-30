package com.spring.boot.exam_service.controller;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.service.TestTrackingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/test-tracking")
@Slf4j
@AllArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class TestTrackingController {
    private final TestTrackingService testTrackingService;

    @GetMapping(value = "/my/{testId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> getMultipleChoiceTest(
            @PathVariable(name = "testId") Long testId){
        return testTrackingService.
                getTestingInProgress(testId);
    }
    @PostMapping(value = "/my/create/{testId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> createTestingInProcess(@PathVariable(name = "testId") Long testId){
            return testTrackingService.createTestingInProcess(testId);
    }
}
