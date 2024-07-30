package com.spring.boot.exam_service.service;

import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateQuestionGroupDTO;
import com.spring.boot.exam_service.dto.request.UpdateQuestionGroupDTO;


public interface QuestionGroupService {

    ApiResponse<?> createQuestionGroup(CreateQuestionGroupDTO dto);

    ApiResponse<?> getAllQuestionGroupOfClassroom(Long classroomId, String search, int page, String column, int size, String sortType, Boolean isEnable);

    ApiResponse<?> switchQuestionGroupStatus(Long questionGroupId, boolean newStatus);

    ApiResponse<?> updateQuestionGroup(Long questionGroupId, UpdateQuestionGroupDTO dto);
}
