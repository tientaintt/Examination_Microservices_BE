package com.spring.boot.exam_service.service;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateMultipleChoiceTestDTO;
import com.spring.boot.exam_service.dto.request.UpdateMultipleChoiceTestDTO;


public interface MultipleChoiceTestService {
    ApiResponse<?> createMultipleChoiceTest(CreateMultipleChoiceTestDTO dto) ;

    ApiResponse<?> deleteMultipleChoiceTest(Long testId);

    ApiResponse<?> updateMultipleChoiceTest(Long testId, UpdateMultipleChoiceTestDTO dto);

    ApiResponse<?> getMultipleChoiceTestsOfClassroom(Long classroomId,boolean isEnded, String search, int page, String column, int size, String sortType);

    ApiResponse<?> getMyMultipleChoiceTests(boolean isEnded, String search, int page, String column, int size, String sortType);

    ApiResponse<?> getMultipleChoiceTest(Long testId);

    ApiResponse<?> getMyMultipleChoiceTestsOf2WeeksAround(String search, int page, String column, int size, String sortType);
    ApiResponse<?> getMyMultipleChoiceTestsNext2Weeks(String search, int page, String column, int size, String sortType);

    ApiResponse<?> getMyMultipleChoiceTestsToday(Long startOfDate,Long endOfDate, String search, int page, String column, int size, String sortType);
    ApiResponse<?> getInfoMultipleChoiceTest(Long testId);

    ApiResponse<?> getMyMultipleChoiceTestsOfClassroom(Long classroomId, boolean isEnded, String search, int page, String column, int size, String sortType);
}
