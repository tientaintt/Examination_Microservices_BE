package com.spring.boot.exam_service.controller;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateSubjectDTO;
import com.spring.boot.exam_service.dto.request.UpdateSubjectDTO;
import com.spring.boot.exam_service.service.SubjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RestController
@RequestMapping("/subject")
@Slf4j
@AllArgsConstructor
public class SubjectController {
    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";

    private SubjectService subjectService;
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping (value="/numberclassmanage",produces = MediaType.APPLICATION_JSON_VALUE)

    public ApiResponse<?> getNumberClassManage(){
        return subjectService.getNumberSubjectManager();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> getMyClassroom(
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
    ){
        return subjectService.
                getMySubjects(search, page, column, size, sortType);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ApiResponse<?> createClassroom(@Valid @RequestBody CreateSubjectDTO DTO){
        log.info("AAAAA");
        return subjectService.createSubject(DTO);
    }
    @DeleteMapping(value = "/delete/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> deleteClassroom(@PathVariable(name = "subjectId") Long subjectId){
        return subjectService.switchSubjectStatus(subjectId, false);
    }
    @PutMapping(value = "/active/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> activeClassroom(@PathVariable(name = "subjectId") Long subjectId){
        return subjectService.switchSubjectStatus(subjectId, true);
    }
    @PutMapping(value = "/update/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> updateClassroom(@PathVariable(name = "subjectId") Long subjectId,
                                             @RequestBody UpdateSubjectDTO DTO){
        return subjectService.updateSubject(subjectId, DTO);
    }
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> getAllEnableClassroom(
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
    ){
        log.info("getAllEnableClassroom");
        return subjectService.getAllSubjectsByStatus(search, page, column, size, sortType, true);
    }
    @GetMapping(value = "/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<?> getMultipleChoiceTest(
            @PathVariable(name = "subjectId") Long subjectId){
        return subjectService.
                getSubjectById(subjectId);
    }
    @GetMapping(value = "/inactive", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> getAllDisableClassroom(
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
    ){
        return subjectService.getAllSubjectsByStatus(search, page, column, size, sortType, false);
    }
}