package com.spring.boot.exam_service.controller;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.service.ReportService;
import com.spring.boot.exam_service.service.impl.ReportServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/report")
@Slf4j
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping(value = "/total",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?>  reportTotal(){
        return reportService.reportTotal();
    }
    @GetMapping(value = "/teacher/total",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?>  reportTeacherTotal(){
        return reportService.reportTeacherTotal();
    }

    @GetMapping(value = "/testByMonth",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?>  reportTestsByMonth(){
        return reportService.reportTestByMonthByUserId();
    }
}
