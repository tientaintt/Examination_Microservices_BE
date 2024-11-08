package com.spring.boot.exam_service.controller;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.AddManageForSubjectDTO;
import com.spring.boot.exam_service.dto.request.AddToSubjectDTO;
import com.spring.boot.exam_service.service.SubjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/teacher")
@Slf4j
@AllArgsConstructor
public class TeacherController {
    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN_TEACHER = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";
    private SubjectService subjectService;
    @PostMapping(value = "/add-manage-subject", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> addTeacherManageForSubject(@Valid @RequestBody AddManageForSubjectDTO DTO){
        return subjectService.addTeacherManageForSubject(DTO);
    }
}
