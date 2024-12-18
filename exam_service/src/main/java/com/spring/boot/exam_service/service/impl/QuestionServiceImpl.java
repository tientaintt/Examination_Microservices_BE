package com.spring.boot.exam_service.service.impl;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateQuestionDTO;
import com.spring.boot.exam_service.dto.request.UpdateQuestionDTO;
import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.dto.response.AnswerResponse;
import com.spring.boot.exam_service.dto.response.QuestionResponse;
import com.spring.boot.exam_service.entity.*;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.*;
import com.spring.boot.exam_service.repository.httpclient.FileClient;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.service.QuestionService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import com.spring.boot.exam_service.utils.EnumParentFileType;
import com.spring.boot.exam_service.utils.PageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionGroupRepository questionGroupRepository;
    private final SubjectRepository subjectRepository;
    private final IdentityService identityService;
    private final QuestionTypeRepository questionTypeRepository;
    private final AnswerRepository answerRepository;
    private final AnswerQuestionRepository answerQuestionRepository;
    private final FileClient fileClient;

    @Override
    public List<Question> getRandomQuestionsByQuestionGroup(Long questionGroupId, Long numberOfQuestion) {
        Optional<QuestionGroup> questionGroup = questionGroupRepository
                .findByIdAndStatus(questionGroupId, true);
        if (questionGroup.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND_ERROR);
        }
        List<Question> questions = questionRepository
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
        log.info("Question group found: " + questionGroupOp.isPresent());
        QuestionType questionType = questionTypeRepository.findByTypeQuestion(dto.getQuestionType()).orElseThrow(
                () -> new AppException(ErrorCode.QUESTION_TYPE_NOT_FOUND_ERROR)
        );
        Question question = Question.builder()
                .content(dto.getContent().trim())
                .questionType(questionType)
                .questionGroup(questionGroupOp.get())
                .build();
        modifyUpdateQuestion(question);
        question = questionRepository.save(question);
        Question finalQuestion = question;
        List<AnswerResponse> answerList = new ArrayList<>();
        dto.getAnswers().stream().forEach((answer -> {
            Optional<Answer> answer1 = answerRepository.findByAnswer(answer.getAnswerContent());
            if (answer1.isEmpty()) {
                Answer save = answerRepository.save(Answer.builder()
                        .answer(answer.getAnswerContent())
                        .build());

                AnswerQuestion answerQuestion = answerQuestionRepository.save(
                        AnswerQuestion.builder()
                                .answer(save)
                                .question(finalQuestion)
                                .isCorrect(answer.getIsCorrect())
                                .build());
                answerList.add(AnswerResponse.builder()
                        .idAnswerQuestion(answerQuestion.getId())
                        .answerContent(save.getAnswer())
                        .isCorrect(answerQuestion.getIsCorrect())
                        .build());

            } else {
                AnswerQuestion answerQuestion = answerQuestionRepository.save(AnswerQuestion.builder()
                        .answer(answer1.get())
                        .question(finalQuestion)
                        .isCorrect(answer.getIsCorrect())
                        .build());
                answerList.add(AnswerResponse.builder()
                        .idAnswerQuestion(answerQuestion.getId())
                        .answerContent(answer.getAnswerContent())
                        .isCorrect(answer.getIsCorrect())
                        .build());

            }

        }));

        QuestionResponse response = CustomBuilder.buildQuestionResponse(question, answerList);
        log.info("Create question: end");
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<?> createQuestionV2(MultipartFile file, CreateQuestionDTO dto) {
        log.info("Create question: start");
        Optional<QuestionGroup> questionGroupOp =
                questionGroupRepository.findByIdAndStatus(dto.getQuestionGroupId(), true);
        log.info("Question group found: " + questionGroupOp.isPresent());
        QuestionType questionType = questionTypeRepository.findByTypeQuestion(dto.getQuestionType()).orElseThrow(
                () -> new AppException(ErrorCode.QUESTION_TYPE_NOT_FOUND_ERROR)
        );
        Question question = Question.builder()
                .content(dto.getContent().trim())
                .questionType(questionType)
                .questionGroup(questionGroupOp.get())
                .build();
        modifyUpdateQuestion(question);
        question = questionRepository.save(question);
        Question finalQuestion = question;
        List<AnswerResponse> answerList = new ArrayList<>();
        dto.getAnswers().stream().forEach((answer -> {
            Optional<Answer> answer1 = answerRepository.findByAnswer(answer.getAnswerContent());
            if (answer1.isEmpty()) {
                Answer save = answerRepository.save(Answer.builder()
                        .answer(answer.getAnswerContent())
                        .build());

                AnswerQuestion answerQuestion = answerQuestionRepository.save(
                        AnswerQuestion.builder()
                                .answer(save)
                                .question(finalQuestion)
                                .isCorrect(answer.getIsCorrect())
                                .build());
                answerList.add(AnswerResponse.builder()
                        .idAnswerQuestion(answerQuestion.getId())
                        .answerContent(save.getAnswer())
                        .isCorrect(answerQuestion.getIsCorrect())
                        .build());

            } else {
                AnswerQuestion answerQuestion = answerQuestionRepository.save(AnswerQuestion.builder()
                        .answer(answer1.get())
                        .question(finalQuestion)
                        .isCorrect(answer.getIsCorrect())
                        .build());
                answerList.add(AnswerResponse.builder()
                        .idAnswerQuestion(answerQuestion.getId())
                        .answerContent(answer.getAnswerContent())
                        .isCorrect(answer.getIsCorrect())
                        .build());

            }

        }));
        String imageUrl=fileClient.saveFile(file,finalQuestion.getQuestionId(), EnumParentFileType.QUESTION_IMAGE.name()).getData().getPath_file();
        QuestionResponse response = CustomBuilder.buildQuestionResponse(question, answerList);
        response.setImageUrl(imageUrl);
        log.info("Create question: end");
        return ApiResponse.builder()
                .data(response)
                .build();

    }

    @Override
    public ApiResponse<?> updateQuestion(Long questionId, UpdateQuestionDTO dto) {
        log.info("Update question: start");
        QuestionType questionType = questionTypeRepository.findByTypeQuestion(dto.getQuestionType()).orElseThrow(
                () -> new AppException(ErrorCode.QUESTION_TYPE_NOT_FOUND_ERROR)
        );
        Optional<Question> questionOp = questionRepository.findByIdAndStatus(questionId, true);
        if (questionOp.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_NOT_FOUND_ERROR);
        }

        Question question = questionOp.get();
        question.setQuestionType(questionType);

        if (Objects.nonNull(dto.getContent())) {
            question.setContent(dto.getContent().trim());
            modifyUpdateQuestion(question);
        }
        answerQuestionRepository.deleteByQuestion(question);
        question = questionRepository.save(question);
        Question finalQuestion = question;
        List<AnswerResponse> answerList = new ArrayList<>();
        dto.getAnswers().stream().forEach((answer -> {
            Optional<Answer> answer1 = answerRepository.findByAnswer(answer.getAnswerContent());
            if (answer1.isEmpty()) {
                Answer save = answerRepository.save(Answer.builder()
                        .answer(answer.getAnswerContent())
                        .build());


                AnswerQuestion answerQuestion = answerQuestionRepository.save(AnswerQuestion.builder()
                        .id(answer.getIdAnswerQuestion())
                        .answer(save)
                        .question(finalQuestion)
                        .isCorrect(answer.getIsCorrect())
                        .build());
                answerList.add(AnswerResponse.builder()
                        .idAnswerQuestion(answerQuestion.getId())
                        .answerContent(save.getAnswer())
                        .isCorrect(answer.getIsCorrect())
                        .build());

            } else {
//                AnswerQuestion answerQuestion = answerQuestionRepository.findByAnswerAndQuestion(answer1.get(), finalQuestion);
               AnswerQuestion answerQuestion= answerQuestionRepository.save(AnswerQuestion.builder()
                                .id(answer.getIdAnswerQuestion())
                        .answer(answer1.get())
                        .question(finalQuestion)
                        .isCorrect(answer.getIsCorrect())
                        .build());
                answerList.add(AnswerResponse.builder()
                        .idAnswerQuestion(answerQuestion.getId())
                        .answerContent(answer.getAnswerContent())
                        .isCorrect(answer.getIsCorrect())
                        .build());

            }

        }));
        QuestionResponse response = CustomBuilder.buildQuestionResponse(question, answerList);
        log.info("Update question: end");
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<?> switchQuestionStatus(Long questionId, boolean newStatus) {
        log.info(String.format("Switch question status to %s: %s", newStatus, "start"));
        Optional<Question> questionOp = questionRepository.findById(questionId);
        if (questionOp.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_NOT_FOUND_ERROR);
        }
        questionOp.get().setIsEnable(newStatus);
        modifyUpdateQuestion(questionOp.get());
        questionRepository.save(questionOp.get());
        log.info(String.format("Switch question status to %s: %s", newStatus, "end"));
        return ApiResponse.builder().build();
    }

    @Override
    public ApiResponse<?> getAllQuestionOfQuestionGroup(Long questionGroupId, String search, int page, String column, int size, String sortType, boolean isActiveQuestion) {
        log.info("Get questions of question group: start, isActiveQuestion: " + isActiveQuestion);
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Optional<QuestionGroup> questionGroupOp =
                questionGroupRepository.findByIdAndStatus(questionGroupId, true);
        if (questionGroupOp.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_GROUP_NOT_FOUND_ERROR);
        }
        Page<Question> questions = questionRepository
                .getQuestionsOfQuestionGroupByQuestionGroupId(questionGroupId, searchText, isActiveQuestion, pageable);
       log.info(searchText);
        List<AnswerResponse> answerResponseList = new ArrayList<>();

        Page<QuestionResponse> response = questions.map(question ->{


                log.info(question.getId().toString());
                return CustomBuilder.buildQuestionResponse(question, answerRepository.findListAnswerByIdQuestion(question.getId()));
        });
        log.info("Get questions of question group: end, isActiveQuestion: " + isActiveQuestion);
        return ApiResponse.builder()
                .data(response)
                .build(
                );
    }

    private void modifyUpdateQuestion(Question question) {
        UserRequest userProfile = identityService.getCurrentUser();
        question.setUpdateBy(userProfile.getId());
        question.setUpdateDate(Instant.now());
    }

//    private String getCorrectAnswerCreate(CreateQuestionDTO dto) {
//        if (dto.getFirstAnswer().getIsCorrect()) {
//            return dto.getFirstAnswer().getAnswerContent();
//        }
//        if (dto.getSecondAnswer().getIsCorrect()) {
//            return dto.getSecondAnswer().getAnswerContent();
//        }
//        if (dto.getThirdAnswer().getIsCorrect()) {
//            return dto.getThirdAnswer().getAnswerContent();
//        }
//        if (dto.getFourthAnswer().getIsCorrect()) {
//            return dto.getFourthAnswer().getAnswerContent();
//        }
//        log.error("There are no correct answer in CreateQuestionDTO");
//        return "";
//    }

    @Override
    public ApiResponse<?> getAllQuestionsOfClassroom(Long subjectId, String search, int page, String column, int size, String sortType, boolean isActiveQuestion) {
        log.info("Get questions of classroom: start, isActiveQuestion: " + isActiveQuestion);
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Optional<Subject> classroom =
                subjectRepository.findActiveSubjectById(subjectId);
        if (classroom.isEmpty()) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        Page<Question> questions = questionRepository
                .getQuestionsOfSubject(subjectId, searchText, isActiveQuestion, pageable);
        Page<QuestionResponse> response = questions.map(question ->
                CustomBuilder.buildQuestionResponse(question, answerRepository.findListAnswerByIdQuestion(question.getId()))
        );
        log.info("Get questions of classroom: end, isActiveQuestion: " + isActiveQuestion);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<?> getQuestionById(Long questionId) {
        Optional<Question>  question= questionRepository.findById(questionId);
        if(question.isEmpty()) {
            throw new AppException(ErrorCode.QUESTION_NOT_FOUND_ERROR);
        }
        String imageUrl=fileClient.getFileRelationshipsByParentIds(Collections.singletonList(question.get().getQuestionId())).getData().stream().findFirst().map(fileRelationship -> fileRelationship.getPath_file()).orElse(null);
        QuestionResponse response= CustomBuilder.buildQuestionResponse(question.get(), answerRepository.findListAnswerByIdQuestion(question.get().getId()));
        response.setImageUrl(imageUrl);

        return ApiResponse.builder()
                .data(response)
                .build();
    }
}
