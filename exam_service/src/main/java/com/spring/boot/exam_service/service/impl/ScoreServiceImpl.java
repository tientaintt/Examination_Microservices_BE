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
import com.spring.boot.exam_service.utils.DateUtils;
import com.spring.boot.exam_service.utils.PageUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import java.util.Collections;
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
    public ApiResponse<?> getScoreOfStudent(String studentId, Long multipleChoiceTestId, int page, String column, int size, String sortType) {
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        UserRequest studentOp = identityService.getAllUserByListId(List.of(studentId)).getFirst();
        if (studentOp == null) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND_ERROR);
        }
        Optional<Score> score = scoreRepository.findByMultipleChoiceTestIdAndUserID(multipleChoiceTestId, studentId);
        if (score.isEmpty()) {
            throw new AppException(ErrorCode.SCORE_NOT_FOUND_ERROR);
        }

        Page<SubmittedQuestion> submittedQuestions = submittedQuestionRepository.findAllByScoreId(score.get().getId(), pageable);
        Page<SubmittedQuestionResponse> submittedQuestionResponses
                = submittedQuestions
                .map(submittedQuestion -> {
                    List<AnswerResponse> listAnswer = answerRepository.findListAnswerByIdQuestion(submittedQuestion.getQuestion().getId());
                    String correctAnswer = answerRepository.findCorrectAnswerByIdQuestion(submittedQuestion.getQuestion().getId());

                    return SubmittedQuestionResponse.builder()
                            .id(submittedQuestion.getId())
                            .correctAnswer(correctAnswer)
                            .submittedAnswer(submittedQuestion.getSubmittedAnswer())
                            .questionType(submittedQuestion.getQuestion().getQuestionType().getTypeQuestion())
                            .answers(listAnswer)
                            .questionId(submittedQuestion.getQuestion().getId())
                            .content(questionRepository.getContentQuestionByQuestionId(submittedQuestion.getQuestion().getId()))
                            .build();
                });
        ScoreResponse response = CustomBuilder.buildScoreResponse(score.get(), submittedQuestionResponses);
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ResponseEntity<InputStreamResource> exportPDFScoreById(Long scoreId) {
        Optional<Score> score = scoreRepository.findById(scoreId);
        if (score.isEmpty()) {
            throw new AppException(ErrorCode.SCORE_NOT_FOUND_ERROR);
        }

        List<SubmittedQuestion> submittedQuestions = submittedQuestionRepository.findAllByScoreId(score.get().getId());
        List<SubmittedQuestionResponse> submittedQuestionResponses = submittedQuestions.stream()
                .map(submittedQuestion -> {
                    List<AnswerResponse> listAnswer = answerRepository.findListAnswerByIdQuestion(submittedQuestion.getQuestion().getId());
                    String correctAnswer = answerRepository.findCorrectAnswerByIdQuestion(submittedQuestion.getQuestion().getId());

                    return SubmittedQuestionResponse.builder()
                            .id(submittedQuestion.getId())
                            .correctAnswer(correctAnswer)
                            .submittedAnswer(submittedQuestion.getSubmittedAnswer())
                            .questionType(submittedQuestion.getQuestion().getQuestionType().getTypeQuestion())
                            .answers(listAnswer)
                            .questionId(submittedQuestion.getQuestion().getId())
                            .content(questionRepository.getContentQuestionByQuestionId(submittedQuestion.getQuestion().getId()))
                            .build();
                }).toList();


        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = null;
            try {
                contentStream = new PDPageContentStream(document, page);
                ClassPathResource fontResource = new ClassPathResource("static/font/Roboto-Black.ttf");
                InputStream fontStream = fontResource.getInputStream();


                PDType0Font robotoBlack = PDType0Font.load(document, fontStream);

                PDType0Font robotoLight = PDType0Font.load(document, new ClassPathResource("static/font/Roboto-Light.ttf").getInputStream());
                contentStream.setFont( robotoBlack,16);

                contentStream.beginText();
                contentStream.newLineAtOffset(300, 750);
                String resultText = score.get().getTotalScore() >= score.get().getTargetScore() ? "Passed" : "Failed";
                setResultColor(contentStream, score.get().getTotalScore() >= score.get().getTargetScore());
                contentStream.showText(resultText);
                contentStream.endText();
                resetColor(contentStream);


                contentStream.beginText();
                contentStream.newLineAtOffset(100, 710);
                contentStream.showText("Exam name: " + score.get().getMultipleChoiceTest().getTestName());
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 670);
                contentStream.showText("Submitted on: " + DateUtils.convertMillisecondsToDate(score.get().getSubmittedDate()));
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(100, 630);
                contentStream.showText("Total score: " + score.get().getTotalScore() + "/10");
                contentStream.endText();

//                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setFont( robotoLight,12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 590);
                contentStream.showText("Submitted Questions and Answers:");
                contentStream.endText();

                float yPosition = 570;
                final float margin = 50;
                final float lineHeight = 20;

                for (SubmittedQuestionResponse response : submittedQuestionResponses) {
                    if (yPosition < margin) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont( robotoLight,12);
                        yPosition = 750;
                    }

                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, yPosition);
                    contentStream.showText("Question: " + response.getContent());
                    contentStream.endText();
                    yPosition -= 20;

                    switch (response.getQuestionType()) {
                        case "Multiple Choice":
                            yPosition = drawMultipleChoice(contentStream, response, yPosition, margin);
                            break;
                        case "Fill in the blank":
                            yPosition = drawFillInBlank(contentStream, response, yPosition);
                            break;
                        case "True/False":
                            yPosition = drawTrueFalse(contentStream, response, yPosition);
                            break;
                    }
                }
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            document.save(outputStream);
            document.close();

            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=ExamResults_" + score.get().getId() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating PDF");
        }
    }

    private void setResultColor(PDPageContentStream contentStream, boolean isCorrect) throws IOException {
        PDColor color = isCorrect
                ? new PDColor(new float[]{0, 1, 0}, PDDeviceRGB.INSTANCE)
                : new PDColor(new float[]{1, 0, 0}, PDDeviceRGB.INSTANCE);
        contentStream.setNonStrokingColor(color);
    }

    private void resetColor(PDPageContentStream contentStream) throws IOException {
        contentStream.setNonStrokingColor(0);
    }

    private void drawCheckbox(PDPageContentStream contentStream, float x, float y, boolean isChecked) throws IOException {
        contentStream.setLineWidth(1f);
        contentStream.addRect(x, y, 10, 10);
        contentStream.stroke();
        if (isChecked) {
            contentStream.moveTo(x, y);
            contentStream.lineTo(x + 10, y + 10);
            contentStream.moveTo(x, y + 10);
            contentStream.lineTo(x + 10, y);
            contentStream.stroke();
        }
    }

    private float drawMultipleChoice(PDPageContentStream contentStream, SubmittedQuestionResponse response, float yPosition, float margin) throws IOException {
        float x = 100;

        for (AnswerResponse answer : response.getAnswers()) {
            drawCheckbox(contentStream, x, yPosition, response.getSubmittedAnswer().equals(answer.getAnswerContent()));
            contentStream.beginText();
            contentStream.newLineAtOffset(x + 20, yPosition);
            contentStream.showText(answer.getAnswerContent());
            contentStream.endText();
            yPosition -= 20;
        }

        yPosition -= 10;
        contentStream.beginText();
        contentStream.newLineAtOffset(x, yPosition);
        contentStream.showText("Correct Answer: " + response.getCorrectAnswer());
        contentStream.endText();

        yPosition -= 20;
        contentStream.beginText();
        contentStream.newLineAtOffset(x, yPosition);
        String resultText = response.getSubmittedAnswer().equals(response.getCorrectAnswer()) ? "Correct" : "Incorrect";
        setResultColor(contentStream, response.getSubmittedAnswer().equals(response.getCorrectAnswer()));
        contentStream.showText(resultText);
        resetColor(contentStream);
        contentStream.endText();

        return yPosition - 30;
    }

    private float drawFillInBlank(PDPageContentStream contentStream, SubmittedQuestionResponse response, float yPosition) throws IOException {
        float x = 100;

        contentStream.beginText();
        contentStream.newLineAtOffset(x, yPosition);
        contentStream.showText("Submitted Answer: " + response.getSubmittedAnswer());
        contentStream.endText();

        yPosition -= 20;
        contentStream.beginText();
        contentStream.newLineAtOffset(x, yPosition);
        contentStream.showText("Correct Answer: " + response.getCorrectAnswer());
        contentStream.endText();

        yPosition -= 20;
        contentStream.beginText();
        contentStream.newLineAtOffset(x, yPosition);
        String resultText = response.getSubmittedAnswer().equals(response.getCorrectAnswer()) ? "Correct" : "Incorrect";
        setResultColor(contentStream, response.getSubmittedAnswer().equals(response.getCorrectAnswer()));
        contentStream.showText(resultText);
        resetColor(contentStream);

        contentStream.endText();

        return yPosition - 30;
    }

    private float drawTrueFalse(PDPageContentStream contentStream, SubmittedQuestionResponse response, float yPosition) throws IOException {
        float x = 100;

        drawCheckbox(contentStream, x, yPosition, "True".equals(response.getSubmittedAnswer()));
        contentStream.beginText();
        contentStream.newLineAtOffset(x + 20, yPosition);
        contentStream.showText("True");
        contentStream.endText();

        yPosition -= 20;

        drawCheckbox(contentStream, x, yPosition, "False".equals(response.getSubmittedAnswer()));
        contentStream.beginText();
        contentStream.newLineAtOffset(x + 20, yPosition);
        contentStream.showText("False");
        contentStream.endText();

        yPosition -= 20;

        contentStream.beginText();
        contentStream.newLineAtOffset(x, yPosition);
        String resultText = response.getSubmittedAnswer().equals(response.getCorrectAnswer()) ? "Correct" : "Incorrect";
        setResultColor(contentStream, response.getSubmittedAnswer().equals(response.getCorrectAnswer()));
        contentStream.showText(resultText);
        resetColor(contentStream);

        contentStream.endText();

        return yPosition - 30;
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
        Page<StudentScoreResponse> scores = scoreRepository.findAllScoreOfMultipleChoiceTest(testId, searchText, pageable);
        scores.forEach(studentScoreResponse -> {
            List<UserRequest> userRequest = identityService.getAllUserByListId(Collections.singletonList(studentScoreResponse.getStudentId()));
            studentScoreResponse.setStudentDisplayName(userRequest.get(0).getDisplayName());
            studentScoreResponse.setStudentLoginName(userRequest.get(0).getLoginName());
        });

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
        if (multipleChoiceTestOp.isEmpty()) {
            throw new AppException(ErrorCode.MULTIPLE_CHOICE_NOT_FOUND_ERROR);
        }
        // Response error if this test has been submitted
        Optional<Score> scoreOp =
                scoreRepository
                        .findByMultipleChoiceTestIdAndUserID(dto.getMultipleChoiceTestId(), userProfile.getId());
        if (scoreOp.isPresent()) {
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
        if (multipleChoiceTestOp.get().getStartDate() > unixTimeNow) {
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
        if (multipleChoiceTestOp.get().getEndDate() < unixTimeNow) {
            isLate = true;
        }

        Double totalScore = 0.00;
        Long totalCorrect = 0L;
        Long totalQuestion = testQuestionRepository.countAllByMultipleChoiceTestId(dto.getMultipleChoiceTestId());
        Double eachQuestionScore = (10 / (double) totalQuestion);

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
            log.info("check question present {}", questionOp.toString());
            if (questionOp.isPresent()) {
                Question question = questionOp.get();
                SubmittedQuestion submittedQuestion = SubmittedQuestion.builder()
                        .question(question)
                        .submittedAnswer(item.getAnswer())
                        .score(score)
                        .build();
                submittedQuestion = submittedQuestionRepository.save(submittedQuestion);
                List<AnswerResponse> listAnswer = answerRepository.findListAnswerByIdQuestion(submittedQuestion.getQuestion().getId());
                String correctAnswer = answerRepository.findCorrectAnswerByIdQuestion(submittedQuestion.getQuestion().getId());
//                AnswerResponse answerResponse=AnswerResponse.builder()
//                        .correctAnswer(correctAnswer)
//                        .answers(listAnswer)
//                        .submittedAnswer(submittedQuestion.getSubmittedAnswer())
//                        .build();

                String questionType = submittedQuestion.getQuestion().getQuestionType().getTypeQuestion();
                log.info("check question type {}", questionType);

                String submittedAnswer = submittedQuestion.getSubmittedAnswer();
                log.info("check submitted answer {}", submittedAnswer);
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
                log.info(question.getId().toString());
                if (answerRepository.findCorrectAnswerByIdQuestion(question.getId()).equals(item.getAnswer())) {
                    totalScore += eachQuestionScore;
                    totalCorrect += 1;
                }
            }
        }
        score.setTotalScore((int) (Math.round(totalScore * 100)) / 100.0);
        score.setTotalCorrect(totalCorrect);
        score = scoreRepository.save(score);

        // Delete test tracking
        Optional<TestTracking> testTracking = testTrackingRepository.findByMultipleChoiceTestIdAndUserID(
                multipleChoiceTestOp.get().getId(), userProfile.getId());
        testTrackingRepository.delete(testTracking.get());

        ScoreResponse response = ScoreResponse.builder()
                .id(score.getId())
                .totalScore(score.getTotalScore())
                .isLate(score.isLate())
                .SubmittedDate(score.getCreatedDate().toEpochMilli())
                .multipleChoiceTest(CustomBuilder.buildMultipleChoiceTestResponse(score.getMultipleChoiceTest()))
                .targetScore(score.getTotalScore())


                .build();
//        log.info(response.getSubmittedQuestions().getFirst().getQuestionType());
//        log.info(response.getSubmittedQuestions().getFirst().getSubmittedAnswer());
        log.info("End execute submitted answer");
        return ApiResponse.builder().data(response).build();
    }

    private void modifyUpdateScore(Question question) {
        UserRequest userProfile = identityService.getCurrentUser();
        question.setUpdateBy(userProfile.getId());
        question.setUpdateDate(Instant.now());
    }

    @Override
    public ApiResponse<?> getAllScoreOfStudent(String userID, String search, Long dateFrom, Long dateTo, int page, String column, int size, String sortType) {
//        Optional<UserProfile> studentOp = userProfileRepository.findById(userID);
        UserRequest studentOp = identityService.getAllUserByListId(List.of(userID)).get(0);
//        if (studentOp.isEmpty()) {
//            throw new AppException(ErrorCode.STUDENT_NOT_FOUND_ERROR) ;
//        }
        String searchText = "%" + search.trim() + "%";
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        Page<MyScoreResponse> response = scoreRepository.findAllMyScores(studentOp.getId(), searchText, dateFrom, dateTo, pageable);
        return ApiResponse.builder().data(response).build();
    }
}
