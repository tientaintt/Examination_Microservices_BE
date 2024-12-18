package com.spring.boot.exam_service.service;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateQuestionDTO;
import com.spring.boot.exam_service.dto.request.UpdateQuestionDTO;
import com.spring.boot.exam_service.entity.Question;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

public interface QuestionService {
    List<Question> getRandomQuestionsByQuestionGroup(Long questionGroupId, Long numberOfQuestion);

    ApiResponse<?> createQuestion(CreateQuestionDTO dto);
    ApiResponse<?> createQuestionV2(MultipartFile file, CreateQuestionDTO dto);

    ApiResponse<?> updateQuestion(Long questionId, UpdateQuestionDTO dto);

    ApiResponse<?> switchQuestionStatus(Long questionId, boolean newStatus);

    ApiResponse<?> getAllQuestionOfQuestionGroup(Long questionGroupId,String search, int page, String column, int size, String sortType, boolean isActiveQuestion);

    ApiResponse<?> getAllQuestionsOfClassroom(Long subjectId, String search, int page, String column, int size, String sortType, boolean isActiveQuestion);

    ApiResponse<?> getQuestionById(Long questionId);


}
