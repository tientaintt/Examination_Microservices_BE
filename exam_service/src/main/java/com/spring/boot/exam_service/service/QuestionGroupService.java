package com.spring.boot.exam_service.service;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateQuestionGroupDTO;
import com.spring.boot.exam_service.dto.request.UpdateQuestionGroupDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


public interface QuestionGroupService {

    ApiResponse<?> createQuestionGroup(CreateQuestionGroupDTO dto);

    ApiResponse<?> getAllQuestionGroupOfClassroom(Long subjectId, String search, int page, String column, int size, String sortType, Boolean isEnable);

    ApiResponse<?> switchQuestionGroupStatus(Long questionGroupId, boolean newStatus);

    ApiResponse<?> updateQuestionGroup(Long questionGroupId, UpdateQuestionGroupDTO dto);
    ApiResponse<?> importQuestionsIntoQuestionGroup(MultipartFile file,long questionGroupId);
    ResponseEntity<InputStreamResource> exportQuestionOfQuestionGroup(Long questionGroupId);
}
