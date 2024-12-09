package com.spring.boot.exam_service.service;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.SubmitMCTestDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;


public interface ScoreService {
   ApiResponse<?> submitTest(SubmitMCTestDTO dto);

   ApiResponse<?> getAllStudentScoreOfTest(Long testId, String search, int page, String column, int size, String sortType);

   ApiResponse<?> getScoreOfStudent(String studentId,Long multipleChoiceTestId,int page,String column,int size,String sortType);
   ResponseEntity<InputStreamResource> exportPDFScoreById(Long scoreId);


   ApiResponse<?> getAllScoreOfStudent( String userID,String search, Long dateFrom, Long dateTo, int page, String column, int size, String sortType);

   ResponseEntity<InputStreamResource> exportScoresOfExam(Long testId);
}
