package com.spring.boot.exam_service.service;


import com.spring.boot.exam_service.dto.ApiResponse;

import com.spring.boot.exam_service.dto.request.AddManageForSubjectDTO;
import com.spring.boot.exam_service.dto.request.AddToSubjectDTO;
import com.spring.boot.exam_service.dto.request.CreateSubjectDTO;
import com.spring.boot.exam_service.dto.request.UpdateSubjectDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


public interface SubjectService {
    ApiResponse<?> getNumberSubjectManager();

    ApiResponse<?> createSubject(CreateSubjectDTO DTO);

    ApiResponse<?> switchSubjectStatus(Long topicId, Boolean newStatus);

    ApiResponse<?> getAllSubjectsByStatus(String search,int page,String column,int size,String sortType, Boolean enable);

    ApiResponse<?> updateSubject(Long subjectId, UpdateSubjectDTO DTO);

    ApiResponse<?> addStudentToSubject(AddToSubjectDTO dto);
    ApiResponse<?> addTeacherManageForSubject(AddManageForSubjectDTO dto);

    ApiResponse<?> getAllStudentOfSubject(Long subjectId, int page,String column,int size,String sortType,String search);

    ApiResponse<?> getMySubjects(String search, int page, String column, int size, String sortType);
    ApiResponse<?> getAllSubjectManagementByIsPrivate(String search, int page, String column, int size, String sortType,boolean isPrivate);
    ApiResponse<?> importStudentsIntoSubject(MultipartFile file,Long subjectId);
    ApiResponse<?> getSubjectById(Long subjectId);

    ApiResponse<?> removeStudentFromSubject(AddToSubjectDTO dto);

    ResponseEntity<Resource> exportStudentsOfSubject(Long subjectId, String typeExport);


}