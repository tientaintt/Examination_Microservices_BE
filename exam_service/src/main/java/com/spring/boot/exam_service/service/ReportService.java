package com.spring.boot.exam_service.service;

import com.spring.boot.exam_service.dto.ApiResponse;
import org.springframework.stereotype.Service;


public interface ReportService {
    ApiResponse<?> reportTotal();
    ApiResponse<?> reportTestByMonthByUserId();
}
