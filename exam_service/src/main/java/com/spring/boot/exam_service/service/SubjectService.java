package com.spring.boot.exam_service.service;


import com.spring.boot.exam_service.dto.ApiResponse;

import com.spring.boot.exam_service.dto.request.AddToSubjectDTO;
import com.spring.boot.exam_service.dto.request.CreateSubjectDTO;
import com.spring.boot.exam_service.dto.request.UpdateSubjectDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;


public interface SubjectService {
    ApiResponse<?> getNumberSubjectManager();

    ApiResponse<?> createSubject(CreateSubjectDTO DTO);

    ApiResponse<?> switchSubjectStatus(Long topicId, Boolean newStatus);

    ApiResponse<?> getAllSubjectsByStatus(String search,int page,String column,int size,String sortType, Boolean enable);

    ApiResponse<?> updateSubject(Long subjectId, UpdateSubjectDTO DTO);

    ApiResponse<?> addStudentToSubject(AddToSubjectDTO dto);

    ApiResponse<?> getAllStudentOfSubject(Long subjectId, int page,String column,int size,String sortType,String search);

    ApiResponse<?> getMySubjects(String search, int page, String column, int size, String sortType);
//
    ApiResponse<?> getSubjectById(Long subjectId);

    ApiResponse<?> removeStudentFromSubject(AddToSubjectDTO dto);

    ResponseEntity<Resource> exportStudentsOfSubject(Long subjectId, String typeExport);
}