package com.spring.boot.exam_service.service;


import com.spring.boot.exam_service.dto.ApiResponse;

import com.spring.boot.exam_service.dto.request.AddToClassroomDTO;
import com.spring.boot.exam_service.dto.request.CreateClassroomDTO;
import com.spring.boot.exam_service.dto.request.UpdateClassroomDTO;


public interface ClassroomService {
    ApiResponse<?> getNumberClassroomManager();

    ApiResponse<?> createClassroom(CreateClassroomDTO DTO);

    ApiResponse<?> switchClassroomStatus(Long topicId, Boolean newStatus);

    ApiResponse<?> getAllClassroomsByStatus(String search,int page,String column,int size,String sortType, Boolean enable);

    ApiResponse<?> updateClassroom(Long classroomId, UpdateClassroomDTO DTO);

    ApiResponse<?> addStudentToClassroom(AddToClassroomDTO dto);

    ApiResponse<?> getAllStudentOfClassroom(Long classroomId, int page,String column,int size,String sortType);

    ApiResponse<?> getMyClassrooms(String search, int page, String column, int size, String sortType);
//
    ApiResponse<?> getClassRoomById(Long classroomId);

    ApiResponse<?> removeStudentFromClassroom(AddToClassroomDTO dto);
}