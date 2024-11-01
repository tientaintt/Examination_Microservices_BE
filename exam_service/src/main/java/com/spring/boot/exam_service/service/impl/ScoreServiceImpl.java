package com.spring.boot.exam_service.service.impl;



import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.SubmitMCTestDTO;
import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.dto.response.*;
import com.spring.boot.exam_service.entity.*;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.*;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.service.ScoreService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import com.spring.boot.exam_service.utils.PageUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ScoreServiceImpl implements ScoreService {
    private final SubmittedQuestionRepository submittedQuestionRepository;
    private final AnswerRepository answerRepository;
    private final IdentityService identityService;
    private final ScoreRepository scoreRepository;
    private final MultipleChoiceTestRepository multipleChoiceTestRepository;
    private final QuestionRepository questionRepository;
    private final TestQuestionRepository testQuestionRepository;

    private final TestTrackingRepository testTrackingRepository;

    @Override
    public ApiResponse<?> getScoreOfStudent(String studentId, Long multipleChoiceTestId) {
        UserRequest studentOp=identityService.getAllUserByListId(List.of(studentId)).getFirst();
        if (studentOp==null) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND_ERROR);
        }
        Optional<Score> score = scoreRepository.findByMultipleChoiceTestIdAndUserID(multipleChoiceTestId, studentId);
        if (score.isEmpty()){
            throw new AppException(ErrorCode.SCORE_NOT_FOUND_ERROR);
        }

        List<SubmittedQuestion> submittedQuestions = submittedQuestionRepository.findAllByScoreId(score.get().getId());
        List<SubmittedQuestionResponse> submittedQuestionResponses
                = submittedQuestions.stream()
                .map(submittedQuestion -> {
                    List<AnswerResponse> listAnswer= answerRepository.findListAnswerByIdQuestion(submittedQuestion.getQuestion().getId());
                    String correctAnswer=answerRepository.findCorrectAnswerByIdQuestion(submittedQuestion.getQuestion().getId());

                    return SubmittedQuestionResponse.builder()
                            .id(submittedQuestion.getId())
                            .correctAnswer(correctAnswer)
                            .submittedAnswer(submittedQuestion.getSubmittedAnswer())
                            .questionType(submittedQuestion.getQuestion().getQuestionType().getTypeQuestion())
                            .answers(listAnswer)
                            .questionId(submittedQuestion.getQuestion().getId())
                            .content(questionRepository.getContentQuestionByQuestionId(submittedQuestion.getQuestion().getId()))
                            .build();
                })
                .collect(Collectors.toList());
        ScoreResponse response = CustomBuilder.buildScoreResponse(score.get(), submittedQuestionResponses);
        return ApiResponse.builder().data(response).build();
    }
    //CHECK
    @Override
    public ApiResponse<?> getAllStudentScoreOfTest(Long testId, String search, int page, String column, int size, String sortType) {
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        Optional<MultipleChoiceTest> MCTestOp = multipleChoiceTestRepository.findById(testId);
        if (MCTestOp.isEmpty()) {
           throw new AppException(ErrorCode.MULTIPLE_CHOICE_NOT_FOUND_ERROR);
        }
        String searchText = "%" + search.trim() + "%";
        Page<StudentScoreResponse> scores =  scoreRepository.findAllScoreOfMultipleChoiceTest(testId, searchText, pageable);
        return ApiResponse.builder()
                .data(scores)
                .build();
    }

    @Override
    public ApiResponse<?> submitTest(SubmitMCTestDTO dto) {
        // Current logged-in student
        UserRequest userProfile = identityService.getCurrentUser();
        // Get the test which student submitted
        Optional<MultipleChoiceTest> multipleChoiceTestOp =
                multipleChoiceTestRepository.findById(dto.getMultipleChoiceTestId());
        if(multipleChoiceTestOp.isEmpty()) {
            throw new AppException(ErrorCode.MULTIPLE_CHOICE_NOT_FOUND_ERROR);
        }
        // Response error if this test has been submitted
        Optional<Score> scoreOp =
                scoreRepository
                        .findByMultipleChoiceTestIdAndUserID(dto.getMultipleChoiceTestId(), userProfile.getId());
        if(scoreOp.isPresent()) {
            // The test is not started
//            LinkedHashMap<String, String> response = new LinkedHashMap<>();
//            response.put(Constants.ERROR_CODE_KEY, ErrorMessage.SCORE_TEST_SUBMITTED.getErrorCode());
//            response.put(Constants.MESSAGE_KEY, ErrorMessage.SCORE_TEST_SUBMITTED.getMessage());
//            return ApiResponse.status(HttpStatus.BAD_REQUEST)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(response);
            throw new AppException(ErrorCode.SCORE_TEST_SUBMITTED_ERROR);
        }
        // Check the time when student submit the test
        boolean isLate = false;
        Long unixTimeNow = Timestamp.from(ZonedDateTime.now().toInstant()).getTime();
        if(multipleChoiceTestOp.get().getStartDate() > unixTimeNow) {
            // The test is not started
//            LinkedHashMap<String, String> response = new LinkedHashMap<>();
//            response.put(Constants.ERROR_CODE_KEY, ErrorMessage.MULTIPLE_CHOICE_TEST_SUBMIT_NOT_STARTED_TEST.getErrorCode());
//            response.put(Constants.MESSAGE_KEY, ErrorMessage.MULTIPLE_CHOICE_TEST_SUBMIT_NOT_STARTED_TEST.getMessage());
//            return ApiResponse.status(HttpStatus.BAD_REQUEST)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(response);
            throw new AppException(ErrorCode.MULTIPLE_CHOICE_TEST_SUBMIT_NOT_STARTED_TEST_ERROR);
        }
        // Submit late
        if(multipleChoiceTestOp.get().getEndDate() < unixTimeNow) {
            isLate = true;
        }

        Double totalScore = 0.00;
        Long totalCorrect = 0L;
        Long totalQuestion = testQuestionRepository.countAllByMultipleChoiceTestId(dto.getMultipleChoiceTestId());
        Double eachQuestionScore =  (10/(double)totalQuestion);

        Score score = Score.builder()
                .isLate(isLate)
                .multipleChoiceTest(multipleChoiceTestOp.get())
                .userID(userProfile.getId())
                .submittedDate(Timestamp.from(ZonedDateTime.now().toInstant()).getTime())
                .targetScore(multipleChoiceTestOp.get().getTargetScore())
                .build();
        score = scoreRepository.save(score);
        List<SubmittedQuestionResponse> submittedQuestionResponses = new ArrayList<>();
        log.info("Start execute submitted answer");
        log.info(dto.getSubmittedAnswers().getFirst().getAnswer());
        for (SubmitMCTestDTO.SubmittedAnswer item : dto.getSubmittedAnswers()) {

            Optional<Question> questionOp = questionRepository.findById(item.getQuestionId());
            log.info("check question present {}",questionOp.toString());
            if (questionOp.isPresent()) {
                Question question = questionOp.get();
                SubmittedQuestion submittedQuestion = SubmittedQuestion.builder()
                        .question(question)
                        .submittedAnswer(item.getAnswer())
                        .score(score)
                        .build();
                submittedQuestion = submittedQuestionRepository.save(submittedQuestion);
                List<AnswerResponse> listAnswer= answerRepository.findListAnswerByIdQuestion(submittedQuestion.getQuestion().getId());
                String correctAnswer=answerRepository.findCorrectAnswerByIdQuestion(submittedQuestion.getQuestion().getId());
//                AnswerResponse answerResponse=AnswerResponse.builder()
//                        .correctAnswer(correctAnswer)
//                        .answers(listAnswer)
//                        .submittedAnswer(submittedQuestion.getSubmittedAnswer())
//                        .build();

                String questionType=submittedQuestion.getQuestion().getQuestionType().getTypeQuestion();
                log.info("check question type {}",questionType);

                String submittedAnswer=submittedQuestion.getSubmittedAnswer();
                log.info("check submitted answer {}",submittedAnswer);
                submittedQuestionResponses.add(
                        SubmittedQuestionResponse.builder()
                                .id(submittedQuestion.getId())
                                .correctAnswer(correctAnswer)
                                .answers(listAnswer)
                                .questionType(questionType)
                                .submittedAnswer(submittedAnswer)
                                .questionId(submittedQuestion.getQuestion().getId())
                                .content(questionRepository.getContentQuestionByQuestionId(submittedQuestion.getQuestion().getId()))
                                .build());

                if(answerRepository.findCorrectAnswerByIdQuestion(question.getId()).equals(item.getAnswer())) {
                    totalScore += eachQuestionScore;
                    totalCorrect += 1;
                }
            }
        }
        score.setTotalCore((int)(Math.round(totalScore * 100))/100.0);
        score.setTotalCorrect(totalCorrect);
        score = scoreRepository.save(score);

        // Delete test tracking
        Optional<TestTracking> testTracking = testTrackingRepository.findByMultipleChoiceTestIdAndUserID(
                multipleChoiceTestOp.get().getId(), userProfile.getId());
        testTrackingRepository.delete(testTracking.get());

        ScoreResponse response = CustomBuilder.buildScoreResponse(score, submittedQuestionResponses);
        log.info(response.getSubmittedQuestions().getFirst().getQuestionType());
        log.info(response.getSubmittedQuestions().getFirst().getSubmittedAnswer());
        log.info("End execute submitted answer");
        return ApiResponse.builder().data(response).build();
    }

    private void modifyUpdateScore(Question question) {
        UserRequest userProfile = identityService.getCurrentUser();
        question.setUpdateBy(userProfile.getId());
        question.setUpdateDate(Instant.now());
    }

    @Override
    public ApiResponse<?> getAllScoreOfStudent(String userID,String search, Long dateFrom, Long dateTo, int page, String column, int size, String sortType) {
//        Optional<UserProfile> studentOp = userProfileRepository.findById(userID);
        UserRequest studentOp=identityService.getAllUserByListId(List.of(userID)).get(0);
//        if (studentOp.isEmpty()) {
//            throw new AppException(ErrorCode.STUDENT_NOT_FOUND_ERROR) ;
//        }
        String searchText = "%" + search.trim() + "%";
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        Page<MyScoreResponse> response = scoreRepository.findAllMyScores(studentOp.getId(),searchText, dateFrom, dateTo, pageable);
        return ApiResponse.builder().data(response).build();
    }
}
