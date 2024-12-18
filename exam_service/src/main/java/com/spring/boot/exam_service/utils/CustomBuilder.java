package com.spring.boot.exam_service.utils;


import com.spring.boot.exam_service.constants.Constants;
import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.request.SubjectNotification;
import com.spring.boot.exam_service.dto.request.TestNotification;
import com.spring.boot.exam_service.dto.response.*;
import com.spring.boot.exam_service.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;

public class CustomBuilder {

//    public static ScoreResponse buildScoreResponse(Score score, List<SubmittedQuestionResponse> submittedQuestionResponses) {
//        return ScoreResponse.builder()
//                .id(score.getId())
//                .totalScore(score.getTotalCore())
//                .isLate(score.isLate())
//                .SubmittedDate(score.getCreatedDate().toEpochMilli())
//                .multipleChoiceTest(CustomBuilder.buildMultipleChoiceTestResponse(score.getMultipleChoiceTest()))
//                .submittedQuestions(submittedQuestionResponses)
//                .targetScore(score.getTargetScore())
//                .build();
//    }
    public static ScoreResponse buildScoreResponse(Score score, Page<SubmittedQuestionResponse> submittedQuestionResponses) {
        return ScoreResponse.builder()
                .id(score.getId())
                .totalScore(score.getTotalScore())
                .isLate(score.isLate())
                .SubmittedDate(score.getCreatedDate().toEpochMilli())
                .multipleChoiceTest(CustomBuilder.buildMultipleChoiceTestResponse(score.getMultipleChoiceTest()))
                .submittedQuestions(submittedQuestionResponses)
                .targetScore(score.getTargetScore())
                .build();
    }
    public static ScoreDTO buildScoreResponse(Score score, List<SubmittedQuestionResponse> submittedQuestionResponses) {
        return ScoreDTO.builder()
                .id(score.getId())
                .totalScore(score.getTotalScore())
                .isLate(score.isLate())
                .SubmittedDate(score.getCreatedDate().toEpochMilli())
                .multipleChoiceTest(CustomBuilder.buildMultipleChoiceTestResponse(score.getMultipleChoiceTest()))
                .submittedQuestions(submittedQuestionResponses)
                .targetScore(score.getTargetScore())
                .build();
    }

//    public static SubmittedQuestionResponse buildSubmittedQuestionResponse(SubmittedQuestion value) {
//        return SubmittedQuestionResponse.builder()
//                .id(value.getId())
//                .questionId(value.getQuestionId())
//                .content(value.getContent())
//                .firstAnswer(value.getFirstAnswer())
//                .secondAnswer(value.getSecondAnswer())
//                .thirdAnswer(value.getThirdAnswer())
//                .fourthAnswer(value.getFourthAnswer())
//                .correctAnswer(value.getCorrectAnswer())
//                .submittedAnswer(value.getSubmittedAnswer())
//                .build();
//    }

    public static MultipleChoiceTestResponse buildMultipleChoiceTestResponse(MultipleChoiceTest multipleChoiceTest) {
        return MultipleChoiceTestResponse.builder()
                .id(multipleChoiceTest.getId())
                .testName(multipleChoiceTest.getTestName())
                .description(multipleChoiceTest.getDescription())
                .startDate(multipleChoiceTest.getStartDate())
                .endDate(multipleChoiceTest.getEndDate())
                .testingTime(multipleChoiceTest.getTestingTime())
                .targetScore(multipleChoiceTest.getTargetScore())
                .build();
    }
    public static TestNotification buildTestNotification(MultipleChoiceTest multipleChoiceTest,List<String> listEmailStudent,Long subjectId,String subjectName) {
        return TestNotification.builder()
                .id(multipleChoiceTest.getId())
                .testName(multipleChoiceTest.getTestName())
                .description(multipleChoiceTest.getDescription())
                .startDate(multipleChoiceTest.getStartDate())
                .endDate(multipleChoiceTest.getEndDate())
                .testingTime(multipleChoiceTest.getTestingTime())
                .subjectId(subjectId)
                .subjectName(subjectName)
                .registerUserEmails(listEmailStudent)
                .build();
    }

//    public static MultipleChoiceTestWithQuestionsResponse buildMultipleChoiceTestWithQuestionsResponse(MultipleChoiceTest multipleChoiceTest, List<QuestionResponse> questions) {
//        return MultipleChoiceTestWithQuestionsResponse.builder()
//                .id(multipleChoiceTest.getId())
//                .testName(multipleChoiceTest.getTestName())
//                .description(multipleChoiceTest.getDescription())
//                .startDate(multipleChoiceTest.getStartDate())
//                .endDate(multipleChoiceTest.getEndDate())
//                .testingTime(multipleChoiceTest.getTestingTime())
//                .questions(questions)
//                .targetScore(multipleChoiceTest.getTargetScore())
//                .build();
//    }
public static MultipleChoiceTestWithQuestionsResponse buildMultipleChoiceTestWithQuestionsResponse(MultipleChoiceTest multipleChoiceTest, Page<QuestionResponse> questions) {
    return MultipleChoiceTestWithQuestionsResponse.builder()
            .id(multipleChoiceTest.getId())
            .testName(multipleChoiceTest.getTestName())
            .description(multipleChoiceTest.getDescription())
            .startDate(multipleChoiceTest.getStartDate())
            .endDate(multipleChoiceTest.getEndDate())
            .testingTime(multipleChoiceTest.getTestingTime())
            .questions(questions)
            .targetScore(multipleChoiceTest.getTargetScore())
            .build();
}
    public static SubjectResponse buildSubjectResponse(Subject classRoom){
        return SubjectResponse.builder()
                .id(classRoom.getId())
                .subjectName(classRoom.getSubjectName())
                .subjectCode(classRoom.getSubjectCode())
                .description(classRoom.getDescription())
                .isPrivate(classRoom.getIsPrivate())
                .isEnable(classRoom.getIsEnable())
                .build();
    }



    public static QuestionGroupResponse buildQuestionGroupResponse(QuestionGroup questionGroup){
        return QuestionGroupResponse.builder()
                .id(questionGroup.getId())
                .name(questionGroup.getName())
                .code(questionGroup.getCode())
                .description(questionGroup.getDescription())
                .isEnable(questionGroup.getIsEnable())
                .build();
    }

    public static QuestionResponse buildQuestionResponse(Question question, List<AnswerResponse> answers){
        return QuestionResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .questionId(question.getQuestionId())
                .questionType(question.getQuestionType().getTypeQuestion())
                .answers(answers)
                .build();
    }
    /**
     * Build an error response when the Classroom is not found
     *
     * @return the response
     */
    public static ResponseEntity<LinkedHashMap<String, String>> buildClassroomNotFoundResponseEntity() {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        response.put(Constants.ERROR_CODE_KEY, ErrorMessage.SUBJECT_NOT_FOUND.getErrorCode());
        response.put(Constants.MESSAGE_KEY, ErrorMessage.SUBJECT_NOT_FOUND.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
    public static ResponseEntity<LinkedHashMap<String, String>> buildQuestionGroupNotFoundResponseEntity() {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        response.put(Constants.ERROR_CODE_KEY, ErrorMessage.QUESTION_GROUP_NOT_FOUND.getErrorCode());
        response.put(Constants.MESSAGE_KEY, ErrorMessage.QUESTION_GROUP_NOT_FOUND.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    public static ResponseEntity<LinkedHashMap<String, String>> buildQuestionNotFoundResponseEntity() {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        response.put(Constants.ERROR_CODE_KEY, ErrorMessage.QUESTION_NOT_FOUND.getErrorCode());
        response.put(Constants.MESSAGE_KEY, ErrorMessage.QUESTION_NOT_FOUND.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    public static ResponseEntity<LinkedHashMap<String, String>> buildMultipleChoiceTestNotFoundResponseEntity() {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        response.put(Constants.ERROR_CODE_KEY, ErrorMessage.MULTIPLE_CHOICE_NOT_FOUND.getErrorCode());
        response.put(Constants.MESSAGE_KEY, ErrorMessage.MULTIPLE_CHOICE_NOT_FOUND.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    public static ResponseEntity<LinkedHashMap<String, String>> buildMultipleChoiceTestTestDateInvalidResponseEntity() {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        response.put(Constants.ERROR_CODE_KEY, ErrorMessage.MULTIPLE_CHOICE_TEST_DATE_INVALID.getErrorCode());
        response.put(Constants.MESSAGE_KEY, ErrorMessage.MULTIPLE_CHOICE_TEST_DATE_INVALID.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    public static ResponseEntity<?> buildStudentNotFoundResponseEntity() {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        response.put(Constants.ERROR_CODE_KEY, ErrorMessage.STUDENT_NOT_FOUND.getErrorCode());
        response.put(Constants.MESSAGE_KEY, ErrorMessage.STUDENT_NOT_FOUND.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    public static ResponseEntity<?> buildScoreNotFoundResponseEntity() {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        response.put(Constants.ERROR_CODE_KEY, ErrorMessage.SCORE_NOT_FOUND.getErrorCode());
        response.put(Constants.MESSAGE_KEY, ErrorMessage.SCORE_NOT_FOUND.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    public static TestTrackingResponse buildTestTrackingResponse(TestTracking testTracking) {
        return TestTrackingResponse.builder()
                .id(testTracking.getId())
                .firstTimeAccess(testTracking.getFirstTimeAccess())
                .dueTime(testTracking.getDueTime())
                .multipleChoiceTestId(testTracking.getMultipleChoiceTest().getId())
                .studentId(testTracking.getUserID())
                .build();
    }

    public static SubjectNotification buildSubjectNotification(Subject result) {
        return SubjectNotification.builder()
                .id(result.getId())
                .subjectCode(result.getSubjectCode())
                .subjectName(result.getSubjectName())
                .build();
    }
}
