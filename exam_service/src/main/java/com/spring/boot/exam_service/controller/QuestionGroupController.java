package com.spring.boot.exam_service.controller;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateQuestionGroupDTO;
import com.spring.boot.exam_service.dto.request.UpdateQuestionGroupDTO;
import com.spring.boot.exam_service.service.QuestionGroupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;


@Validated
@RestController
@RequestMapping("/question-group")
@Slf4j
@AllArgsConstructor
public class QuestionGroupController {

    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";

    private final QuestionGroupService questionGroupService;

    @GetMapping(value = "/subject/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getAllActiveQuestionGroupOfClassroom(
            @PathVariable(name = "subjectId") Long subjectId,
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        return questionGroupService.getAllQuestionGroupOfClassroom(subjectId, search, page, column, size, sortType, true);
    }

    @GetMapping(value = "/inactive/subject/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getAllInactiveQuestionGroupOfClassroom(
            @PathVariable(name = "subjectId") Long subjectId,
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        return questionGroupService.getAllQuestionGroupOfClassroom(subjectId, search, page, column, size, sortType, false);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> createQuestionGroup(@Valid @RequestBody CreateQuestionGroupDTO DTO){
        return questionGroupService.createQuestionGroup(DTO);
    }

    @DeleteMapping(value = "/delete/{questionGroupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> deleteQuestionGroup(@PathVariable(name = "questionGroupId") Long questionGroupId){
        return questionGroupService.switchQuestionGroupStatus(questionGroupId, false);
    }

    @PutMapping(value = "/active/{questionGroupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> activeQuestionGroup(@PathVariable(name = "questionGroupId") Long questionGroupId){
        return questionGroupService.switchQuestionGroupStatus(questionGroupId, true);
    }

    @PutMapping(value = "/update/{questionGroupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> updateQuestionGroup(@PathVariable(name = "questionGroupId") Long questionGroupId,
                                                 @Valid @RequestBody UpdateQuestionGroupDTO DTO){
        return questionGroupService.updateQuestionGroup(questionGroupId, DTO);
    }

    @GetMapping(value ="export/questions/{questionGroupId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<InputStreamResource> exportListQuestionOfQuestionGroup(@PathVariable(name = "questionGroupId") Long questionGroupId)
    {
        return questionGroupService.exportQuestionOfQuestionGroup(questionGroupId);
    }

    @PostMapping(value = "/import/questions/{questionGroupId}",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> importQuestionsIntoQuestionGroup(@RequestPart MultipartFile file, @PathVariable(name = "questionGroupId") Long questionGroupId) {
        return questionGroupService.importQuestionsIntoQuestionGroup(file,questionGroupId);
    }

}
