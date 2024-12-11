package com.spring.boot.exam_service.controller;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.AddToSubjectDTO;
import com.spring.boot.exam_service.service.SubjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@Validated
@RestController
@RequestMapping("/student")
@Slf4j
@AllArgsConstructor
public class StudentController {

    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN_STUDENT = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";
    private SubjectService subjectService;
    @PostMapping(value = "/add-to-class", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> addStudentToClassroom(@Valid @RequestBody AddToSubjectDTO DTO){
        return subjectService.addStudentToSubject(DTO);
    }

    @PostMapping(value = "/remove-from-class", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> removeStudentFromClassroom(@Valid @RequestBody AddToSubjectDTO DTO){
        return subjectService.removeStudentFromSubject(DTO);
    }

    @GetMapping(value = "/subject/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getAllStudentsOfClassroom(
            @PathVariable(name = "subjectId") Long subjectId,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN_STUDENT) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType,
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search){
        return subjectService.getAllStudentOfSubject(subjectId, page, column, size, sortType,search);
    }

    @GetMapping(value = "/export/subject/{subjectId}",produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Resource> exportStudentsOfClassroom(@PathVariable(name = "subjectId") Long subjectId,
                                                              @RequestParam String typeExport){
       return subjectService.exportStudentsOfSubject(subjectId,typeExport);
    }

//    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
//    public ApiResponse<?> getAllActiveStudents(
//            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
//            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//            @RequestParam(defaultValue = DEFAULT_COLUMN_STUDENT) String column,
//            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
//            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
//    ){
//        return userProfileService.getAllStudentsByStatus(search, page, column, size, sortType, true);
//    }
//
//    @GetMapping(value = "/inactive", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
//    public ApiResponse<?> getAllDisableStudents(
//            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
//            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//            @RequestParam(defaultValue = DEFAULT_COLUMN_STUDENT) String column,
//            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
//            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
//    ){
//        return userProfileService.getAllStudentsByStatus(search, page, column, size, sortType, false);
//    }
//
//    @GetMapping(value = "/verified", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
//    public ApiResponse<?> getAllVerifiedStudents(
//            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
//            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//            @RequestParam(defaultValue = DEFAULT_COLUMN_STUDENT) String column,
//            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
//            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
//    ){
//        return userProfileService.getAllVerifiedStudents(search, page, column, size, sortType);
//    }
}
