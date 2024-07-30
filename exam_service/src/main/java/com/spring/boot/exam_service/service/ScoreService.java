package com.spring.boot.exam_service.service;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.SubmitMCTestDTO;


public interface ScoreService {
   ApiResponse<?> submitTest(SubmitMCTestDTO dto);

   ApiResponse<?> getAllStudentScoreOfTest(Long testId, String search, int page, String column, int size, String sortType);

   ApiResponse<?> getScoreOfStudent(Long studentId,Long multipleChoiceTestId);


   ApiResponse<?> getAllScoreOfStudent( Long userID,String search, Long dateFrom, Long dateTo, int page, String column, int size, String sortType);
}
