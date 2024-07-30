package com.spring.boot.exam_service.service.impl;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateQuestionDTO;
import com.spring.boot.exam_service.dto.request.UpdateQuestionDTO;
import com.spring.boot.exam_service.dto.response.QuestionResponse;
import com.spring.boot.exam_service.entity.Classroom;
import com.spring.boot.exam_service.entity.Question;
import com.spring.boot.exam_service.entity.QuestionGroup;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.ClassroomRepository;
import com.spring.boot.exam_service.repository.QuestionGroupRepository;
import com.spring.boot.exam_service.repository.QuestionRepository;
import com.spring.boot.exam_service.service.QuestionService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import com.spring.boot.exam_service.utils.PageUtils;
import com.spring.boot.exam_service.utils.WebUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final ClassroomRepository classroomRepository;
    private final WebUtils webUtils;

    @Override
    public List<Question> getRandomQuestionsByQuestionGroup(Long questionGroupId, Long numberOfQuestion) {
        Optional<QuestionGroup> questionGroup = questionGroupRepository
                .findByIdAndStatus(questionGroupId, true);
        if(questionGroup.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND_ERROR);
        }
        List<Question> questions =  questionRepository
                .findRandomQuestionByQuestionGroupId(questionGroupId, numberOfQuestion);
        if (questions.size() < numberOfQuestion) {
            throw new AppException(ErrorCode.NOT_ENOUGH_QUESTION_ERROR);
        }
        return questions;
    }
    
    @Override
    public ApiResponse<?> createQuestion(CreateQuestionDTO dto) {
        log.info("Create question: start");
        Optional<QuestionGroup> questionGroupOp =
                questionGroupRepository.findByIdAndStatus(dto.getQuestionGroupId(), true);

        Question question = Question.builder()
                .content(dto.getContent().trim())
                .firstAnswer(dto.getFirstAnswer().getAnswerContent().trim())
                .secondAnswer(dto.getSecondAnswer().getAnswerContent().trim())
                .thirdAnswer(dto.getThirdAnswer().getAnswerContent().trim())
                .fourthAnswer(dto.getFourthAnswer().getAnswerContent().trim())
                .correctAnswer(getCorrectAnswerCreate(dto))
                .questionGroup(questionGroupOp.get())
                .build();
        modifyUpdateQuestion(question);
        question = questionRepository.save(question);
        QuestionResponse response = CustomBuilder.buildQuestionResponse(question);
        log.info("Create question: end");
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<?> updateQuestion(Long questionId, UpdateQuestionDTO dto) {
        log.info("Update question: start");
        Optional<Question> questionOp = questionRepository.findByIdAndStatus(questionId, true);
        if(questionOp.isEmpty()) {
           throw new AppException(ErrorCode.QUESTION_NOT_FOUND_ERROR);
        }

        Question question = questionOp.get();
        String correctAnswer = question.getCorrectAnswer();

        if(Objects.nonNull(dto.getContent())) {
            question.setContent(dto.getContent().trim());
            modifyUpdateQuestion(question);
        }
        if(Objects.nonNull(dto.getFirstAnswer())) {
            String answerContent = dto.getFirstAnswer().getAnswerContent().trim();
            question.setFirstAnswer(answerContent);
            if(dto.getFirstAnswer().getIsCorrect()) {
                correctAnswer = answerContent;
            }
            modifyUpdateQuestion(question);
        }
        if(Objects.nonNull(dto.getSecondAnswer())) {
            String answerContent = dto.getSecondAnswer().getAnswerContent().trim();
            question.setSecondAnswer(answerContent);
            if(dto.getSecondAnswer().getIsCorrect()) {
                correctAnswer = answerContent;
            }
            modifyUpdateQuestion(question);
        }
        if(Objects.nonNull(dto.getThirdAnswer())) {
            String answerContent = dto.getThirdAnswer().getAnswerContent().trim();
            question.setThirdAnswer(answerContent);
            if(dto.getThirdAnswer().getIsCorrect()) {
                correctAnswer = answerContent;
            }
            modifyUpdateQuestion(question);
        }
        if(Objects.nonNull(dto.getFourthAnswer())) {
            String answerContent = dto.getFourthAnswer().getAnswerContent().trim();
            question.setFourthAnswer(answerContent);
            if(dto.getFourthAnswer().getIsCorrect()) {
                correctAnswer = answerContent;
            }
            modifyUpdateQuestion(question);
        }
        question.setCorrectAnswer(correctAnswer);
        question = questionRepository.save(question);
        QuestionResponse response = CustomBuilder.buildQuestionResponse(question);
        log.info("Update question: end");
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<?> switchQuestionStatus(Long questionId, boolean newStatus) {
        log.info(String.format("Switch question status to %s: %s", newStatus, "start"));
        Optional<Question> questionOp = questionRepository.findById(questionId);
        if(questionOp.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_NOT_FOUND_ERROR);
        }
        questionOp.get().setIsEnable(newStatus);
        modifyUpdateQuestion(questionOp.get());
        questionRepository.save(questionOp.get());
        log.info(String.format("Switch question status to %s: %s", newStatus, "end"));
        return ApiResponse.builder().build();
    }

    @Override
    public ApiResponse<?> getAllQuestionOfQuestionGroup(Long questionGroupId,String search, int page, String column, int size, String sortType, boolean isActiveQuestion) {
        log.info("Get questions of question group: start, isActiveQuestion: "+isActiveQuestion);
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Optional<QuestionGroup> questionGroupOp =
                questionGroupRepository.findByIdAndStatus(questionGroupId, true);
        if (questionGroupOp.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND_ERROR);
        }
        Page<Question> questions = questionRepository
                .getQuestionsOfQuestionGroupByQuestionGroupId(questionGroupId, searchText, isActiveQuestion, pageable);
        Page<QuestionResponse> response = questions.map(CustomBuilder::buildQuestionResponse);
        log.info("Get questions of question group: end, isActiveQuestion: "+isActiveQuestion);
        return ApiResponse.builder()
                .data(response)
                .build(
        );
    }

    private void modifyUpdateQuestion(Question question) {
//        UserProfile userProfile = webUtils.getCurrentLogedInUser();
//        question.setUpdateBy(userProfile.getLoginName());
//        question.setUpdateDate(Instant.now());
    }

    private String getCorrectAnswerCreate(CreateQuestionDTO dto){
        if (dto.getFirstAnswer().getIsCorrect()){
            return dto.getFirstAnswer().getAnswerContent();
        }
        if (dto.getSecondAnswer().getIsCorrect()){
            return dto.getSecondAnswer().getAnswerContent();
        }
        if (dto.getThirdAnswer().getIsCorrect()){
            return dto.getThirdAnswer().getAnswerContent();
        }
        if (dto.getFourthAnswer().getIsCorrect()){
            return dto.getFourthAnswer().getAnswerContent();
        }
        log.error("There are no correct answer in CreateQuestionDTO");
        return "";
    }

    @Override
    public ApiResponse<?> getAllQuestionsOfClassroom(Long classroomId, String search, int page, String column, int size, String sortType, boolean isActiveQuestion) {
        log.info("Get questions of classroom: start, isActiveQuestion: "+isActiveQuestion);
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Optional<Classroom> classroom =
                classroomRepository.findActiveClassroomById(classroomId);
        if (classroom.isEmpty()) {
            throw new AppException(ErrorCode.CLASSROOM_NOT_FOUND);
        }
        Page<Question> questions = questionRepository
                .getQuestionsOfClassroom(classroomId, searchText, isActiveQuestion, pageable);
        Page<QuestionResponse> response = questions.map(CustomBuilder::buildQuestionResponse);
        log.info("Get questions of classroom: end, isActiveQuestion: "+isActiveQuestion);
        return ApiResponse.builder()
                .data(response)
                .build();
    }
}
