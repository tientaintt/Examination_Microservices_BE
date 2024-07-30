package com.spring.boot.exam_service.service;


import com.spring.boot.exam_service.dto.ApiResponse;

public interface TestTrackingService {
    ApiResponse<?> getTestingInProgress(Long testId);

    ApiResponse<?> createTestingInProcess(Long testId);
}
