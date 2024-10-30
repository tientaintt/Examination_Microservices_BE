package com.spring.boot.exam_service.controller;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateQuestionDTO;
import com.spring.boot.exam_service.dto.request.UpdateQuestionDTO;
import com.spring.boot.exam_service.service.QuestionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@Validated
@RestController
@RequestMapping("/question")
@Slf4j
@AllArgsConstructor
public class QuestionController {
    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";
    private QuestionService questionService;
    @GetMapping(value = "/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getQuestion(
            @PathVariable(name = "questionId") Long questionId
            ){
        return questionService.getQuestionById(questionId);
    }

    @GetMapping(value = "/inactive/subject/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getAllInactiveQuestionOfClassroom(
            @PathVariable(name = "subjectId") Long subjectId,
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        return questionService.getAllQuestionsOfClassroom(subjectId, search, page, column, size, sortType, false);
    }

    @GetMapping(value = "/subject/{subjectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getAllActiveQuestionOfClassroom(
            @PathVariable(name = "subjectId") Long subjectId,
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        return questionService.getAllQuestionsOfClassroom(subjectId, search, page, column, size, sortType, true);
    }

    @GetMapping(value = "/question-group/{questionGroupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getAllActiveQuestionOfQuestionGroup(
            @PathVariable(name = "questionGroupId") Long questionGroupId,
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        return questionService.getAllQuestionOfQuestionGroup(questionGroupId, search, page, column, size, sortType, true);
    }
    @GetMapping(value = "/inactive/question-group/{questionGroupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> getAllInactiveQuestionOfQuestionGroup(
            @PathVariable(name = "questionGroupId") Long questionGroupId,
            @RequestParam(defaultValue = DEFAULT_SEARCH) String search,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
            @RequestParam(defaultValue = DEFAULT_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        return questionService.getAllQuestionOfQuestionGroup(questionGroupId, search, page, column, size, sortType, false);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> createQuestion(@Valid @RequestBody CreateQuestionDTO DTO){
        return questionService.createQuestion(DTO);
    }
    @PutMapping(value = "/update/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> updateQuestion(@PathVariable(name = "questionId") Long questionId,
                                                 @Valid @RequestBody UpdateQuestionDTO DTO){
        return questionService.updateQuestion(questionId, DTO);
    }
    @DeleteMapping(value = "/delete/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> deleteQuestion(@PathVariable(name = "questionId") Long questionId){
        return questionService.switchQuestionStatus(questionId, false);
    }

    @PutMapping(value = "/active/{questionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ApiResponse<?> activeClassroom(@PathVariable(name = "questionId") Long questionId){
        return questionService.switchQuestionStatus(questionId, true);
    }
}
