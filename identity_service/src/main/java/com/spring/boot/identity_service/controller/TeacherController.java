package com.spring.boot.identity_service.controller;

import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TeacherController {
    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN_TEACHER = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";
    UserService userService;
    @GetMapping(value = "/verified", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<?> getAllVerifiedStudents(
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN_TEACHER) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
    ) {
        return userService.getAllVerifiedTeachers(search, page, column, size, sortType);
    }
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<?> getAllActiveTeacher(@RequestParam(defaultValue = DEFAULT_SEARCH) String search,
                                              @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                              @RequestParam(defaultValue = DEFAULT_COLUMN_TEACHER) String column,
                                              @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                              @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType) {
        return userService.getAllTeachersByStatus(search, page, column, size, sortType, true);
    }

    @GetMapping(value = "/inactive", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<?> getAllInActiveTeachers(@RequestParam(defaultValue = DEFAULT_SEARCH) String search,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_COLUMN_TEACHER) String column,
                                                 @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                                 @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType
    ) {
        return userService.getAllTeachersByStatus(search, page, column, size, sortType, false);
    }
}
