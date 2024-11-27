package com.spring.boot.exam_service.controller;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.GetScoreOfStudentDTO;
import com.spring.boot.exam_service.dto.request.SubmitMCTestDTO;
import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.service.ScoreService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.sql.Timestamp;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Objects;

@Validated
@RestController
@RequestMapping("/score")
@Slf4j
@AllArgsConstructor
public class ScoreController {
    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";
    private final ScoreService scoreService;
    private final IdentityService identityService;
    @PostMapping(value = "/submit-test", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> submitTest(@Valid @RequestBody SubmitMCTestDTO DTO){
        return scoreService.submitTest(DTO);
    }
    @GetMapping(value = "/multiple-choice-test/{testId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getAllStudentScoreOfTest(
            @PathVariable(name = "testId") Long testId,
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        return scoreService.getAllStudentScoreOfTest(testId, search, page, column, size, sortType);
    }
    @GetMapping(value = "/export/{scoreId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN','STUDENT')")
    public ResponseEntity<InputStreamResource>  exportPDFScoreById(@PathVariable(name = "scoreId") Long scoreId){

        return scoreService.exportPDFScoreById(scoreId);
    }


    @PostMapping(value = "/student", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getScoreOfStudent(@Valid @RequestBody GetScoreOfStudentDTO dto,
                                            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
                                            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        return scoreService.getScoreOfStudent(dto.getStudentId(), dto.getMultipleChoiceTestId(),page,column,size,sortType);
    }
    @GetMapping(value = "/my/{testId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> getMyScoreOfTest(@PathVariable Long testId,
                                           @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
                                           @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                           @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        UserRequest userProfile = identityService.getCurrentUser();
        return scoreService.getScoreOfStudent(userProfile.getId(), testId,page,column,size,sortType);
    }
    @GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<?> getMyAllScores(
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(required = false) Long dateFrom,
            @RequestParam(required = false) Long dateTo,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType

    ){

        Long dateFromDefault = Timestamp.from(ZonedDateTime.now().toInstant().minus(Period.ofWeeks(4))).getTime();
        Long dateToDefault = Timestamp.from(ZonedDateTime.now().toInstant()).getTime();
        if(Objects.isNull(dateFrom)){
            dateFrom = dateFromDefault;
        }
        if(Objects.isNull(dateTo)){
            dateTo = dateToDefault;
        }
        UserRequest userProfile = identityService.getCurrentUser();
        return scoreService.getAllScoreOfStudent(userProfile.getId(),search, dateFrom, dateTo, page, column, size, sortType);
    }
}
