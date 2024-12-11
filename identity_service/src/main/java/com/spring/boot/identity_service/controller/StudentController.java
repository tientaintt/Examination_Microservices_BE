package com.spring.boot.identity_service.controller;

import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.service.StudentService;
import com.spring.boot.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudentController {

    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN_STUDENT = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";

    UserService userService;

    @GetMapping(value="/total",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public APIResponse<?> getAllTotalStudents(){
        return userService.getTotalStudents();
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public APIResponse<?> getAllActiveStudent(@RequestParam(defaultValue = DEFAULT_SEARCH) String search,
                                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_COLUMN_STUDENT) String column,
                                              @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                              @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType) {
        return userService.getAllStudentsByStatus(search, page, column, size, sortType, true);
    }

    @GetMapping(value = "/inactive", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public APIResponse<?> getAllInActiveStudents(@RequestParam(defaultValue = DEFAULT_SEARCH) String search,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_COLUMN_STUDENT) String column,
                                                 @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                                 @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
    ) {
        return userService.getAllStudentsByStatus(search, page, column, size, sortType, false);
    }

    @GetMapping(value = "/verified", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public APIResponse<?> getAllVerifiedStudents(
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN_STUDENT) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
    ) {
        return userService.getAllVerifiedStudents(search, page, column, size, sortType);
    }

    @GetMapping(value = "/all_id", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public APIResponse<?> getAllStudentId() {
        return userService.getAllStudentId();
    }

    @GetMapping(value = "/export/verified",produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> exportStudentsVerified(@RequestParam String typeExport){
        return userService.exportStudentsVerified(typeExport);
    }
}
