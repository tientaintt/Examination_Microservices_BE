package com.spring.boot.exam_service.service.impl;


import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.CreateMultipleChoiceTestDTO;
import com.spring.boot.exam_service.dto.request.NotificationRequest;
import com.spring.boot.exam_service.dto.request.UpdateMultipleChoiceTestDTO;
import com.spring.boot.exam_service.dto.request.UserRequest;
import com.spring.boot.exam_service.dto.response.*;

import com.spring.boot.exam_service.entity.*;
import com.spring.boot.exam_service.exception.AppException;
import com.spring.boot.exam_service.exception.ErrorCode;
import com.spring.boot.exam_service.repository.*;
import com.spring.boot.exam_service.repository.httpclient.NotificationClient;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.service.MultipleChoiceTestService;
import com.spring.boot.exam_service.service.QuestionService;
import com.spring.boot.exam_service.utils.CustomBuilder;
import com.spring.boot.exam_service.utils.PageUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MultipleChoiceTestServiceImpl implements MultipleChoiceTestService {
    TestQuestionRepository testQuestionRepository;
    AnswerRepository answerRepository;
    MultipleChoiceTestRepository multipleChoiceTestRepository;
    SubjectRepository subjectRepository;
    QuestionRepository questionRepository;
    QuestionService questionService;
    //    private final MailService mailService;
    IdentityService identityService;
    ScoreRepository scoreRepository;
    private final NotificationClient notificationClient;

    @Override
    public ApiResponse<?> getMultipleChoiceTest(Long testId,int page, String column, int size, String sortType) {
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String myId = identityService.getCurrentUser().getId();
        Optional<MultipleChoiceTest> multipleChoiceTestOp = multipleChoiceTestRepository.findById(testId);
        if (multipleChoiceTestOp.isEmpty()) {
            throw new AppException(ErrorCode.MULTIPLE_CHOICE_NOT_FOUND_ERROR);
        }
        MultipleChoiceTest multipleChoiceTest = multipleChoiceTestOp.get();
        List<Long> questionIds = testQuestionRepository.findQuestionIdsOfTest(multipleChoiceTest.getId());
        Page<Question> questions = questionRepository.findAllByIds(questionIds,pageable);
        Page<QuestionResponse> questionsOfTheTest =
                questions.map(question -> CustomBuilder.buildQuestionResponse(question,answerRepository.findListAnswerByIdQuestion(question.getId())));
        MultipleChoiceTestWithQuestionsResponse response =
                CustomBuilder.buildMultipleChoiceTestWithQuestionsResponse(multipleChoiceTest, questionsOfTheTest);
        Optional<Score> score = scoreRepository.findByMultipleChoiceTestIdAndUserID(response.getId(), myId);
        response.setIsSubmitted(score.isPresent());

        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> getMyMultipleChoiceTestsNext2Weeks(String search, int page, String column, int size, String sortType) {
        String myId = identityService.getCurrentUser().getId();
//        Long unixTime2WeeksAgo = Date.from(Instant.now()).getTime();
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Long unixTime2WeeksAgo = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        Long unixTime2WeeksLater = Timestamp.from(ZonedDateTime.now().toInstant().plus(Period.ofWeeks(2))).getTime();
        List<MyMultipleChoiceTestResponse> response =
                multipleChoiceTestRepository.find2WeeksAroundMCTest(myId, unixTime2WeeksAgo, unixTime2WeeksLater, searchText, pageable);
        return ApiResponse.builder()
                .data(response).build();
    }

    @Override
    public ApiResponse<?> getMyMultipleChoiceTestsOf2WeeksAround(String search, int page, String column, int size, String sortType) {
        String myId = identityService.getCurrentUser().getId();
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Long unixTime2WeeksAgo = Timestamp.from(ZonedDateTime.now().toInstant().minus(Period.ofWeeks(2))).getTime();
        Long unixTime2WeeksLater = Timestamp.from(ZonedDateTime.now().toInstant().plus(Period.ofWeeks(2))).getTime();
        List<MyMultipleChoiceTestResponse> response =
                multipleChoiceTestRepository.find2WeeksAroundMCTest(myId, unixTime2WeeksAgo, unixTime2WeeksLater, searchText, pageable);
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> getTeacherMultipleChoiceTestsOf2WeeksAround(String search, int page, String column, int size, String sortType) {
        String myId = identityService.getCurrentUser().getId();
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Long unixTime2WeeksAgo = Timestamp.from(ZonedDateTime.now().toInstant().minus(Period.ofWeeks(2))).getTime();
        Long unixTime2WeeksLater = Timestamp.from(ZonedDateTime.now().toInstant().plus(Period.ofWeeks(2))).getTime();
        List<MyMultipleChoiceTestResponse> response =
                multipleChoiceTestRepository.findMCTestOfSubjectManagerAroundTwoWeek(myId, unixTime2WeeksAgo, unixTime2WeeksLater, searchText, pageable);
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> getAllMultipleChoiceTestsManagement(String search, int page, String column, int size, String sortType, Long startOfDate, Long endOfDate) {
        String myId = identityService.getCurrentUser().getId();
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<MyMultipleChoiceTestResponse> response ;
        if(Objects.nonNull(endOfDate)){
            response =
                    multipleChoiceTestRepository.findMCTestManagementByDay(myId,searchText,startOfDate,endOfDate, pageable);

        } else {
            response = multipleChoiceTestRepository.
                    findNotEndedMultipleChoiceTestsManagement(myId,startOfDate, searchText, pageable);
        }
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> getMyMultipleChoiceTestsToday(Long startOfDate, Long endOfDate, String search, int page, String column, int size, String sortType) {

        String  myId = identityService.getCurrentUser().getId();
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Page<MyMultipleChoiceTestResponse> response ;
        if(Objects.nonNull(endOfDate)){
            response =
                    multipleChoiceTestRepository.findMCTestByDay(myId, startOfDate ,endOfDate,searchText,pageable);
        } else {
            response = multipleChoiceTestRepository.
                    findMyNotEndedMultipleChoiceTests(myId,startOfDate, searchText, pageable);
        }
        response.forEach((item)->{
            Optional<Score> score = scoreRepository.findByMultipleChoiceTestIdAndUserID(item.getId(), myId);
            if (score.isPresent()) {
                item.setIsSubmitted(Boolean.TRUE);
            }
        });
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> getInfoMultipleChoiceTest(Long testId) {
        String  myId = identityService.getCurrentUser().getId();
        MyMultipleChoiceTestResponse response =
                multipleChoiceTestRepository.findMultipleChoiceTestInformation(testId, myId);
        if (Objects.isNull(response)) {
           throw new AppException(ErrorCode.MULTIPLE_CHOICE_NOT_FOUND_ERROR);
        }
        return ApiResponse.builder().data(response).build();
    }




    @Override
    public ApiResponse<?> getMyMultipleChoiceTests(boolean isEnded, String search, int page, String column, int size, String sortType) {
        String  myId =identityService.getCurrentUser().getId();
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        String searchText = "%" + search.trim() + "%";
        Long unixTimeNow = Timestamp.from(ZonedDateTime.now().toInstant()).getTime();
        Page<MyMultipleChoiceTestResponse> multipleChoiceTests;
        if (isEnded) {
            multipleChoiceTests = multipleChoiceTestRepository.
                    findMyEndedMultipleChoiceTests(myId,unixTimeNow, searchText, pageable);
        } else {
            multipleChoiceTests = multipleChoiceTestRepository.
                    findMyNotEndedMultipleChoiceTests(myId,unixTimeNow, searchText, pageable);
        }
        return ApiResponse.builder().data(multipleChoiceTests).build();

    }

    @Override
    public ApiResponse<?> getMultipleChoiceTestsOfClassroom(Long subjectId, boolean isEnded, String search, int page, String column, int size, String sortType) {
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        Optional<Subject> classRoom = subjectRepository.findById(subjectId);
        if (classRoom.isEmpty()) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        String searchText = "%" + search.trim() + "%";
        Long unixTimeNow = Timestamp.from(ZonedDateTime.now().toInstant()).getTime();
        Page<MultipleChoiceTest> multipleChoiceTests;
        if (isEnded) {
            multipleChoiceTests = multipleChoiceTestRepository.
                    findEndedMultipleChoiceTestOfClassroomByClassroomId(subjectId, unixTimeNow, searchText, pageable);
        } else {
            multipleChoiceTests = multipleChoiceTestRepository.
                    findNotEndedMultipleChoiceTestOfClassroomByClassroomId(subjectId, unixTimeNow, searchText, pageable);
        }
        Page<MultipleChoiceTestResponse> response = multipleChoiceTests.map(CustomBuilder::buildMultipleChoiceTestResponse);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<?> getMyMultipleChoiceTestsOfClassroom(Long subjectId, boolean isEnded, String search, int page, String column, int size, String sortType) {
        String myId = identityService.getCurrentUser().getId();
        Pageable pageable = PageUtils.createPageable(page, size, sortType, column);
        Optional<Subject> classRoom =  subjectRepository.findById(subjectId);
        if (classRoom.isEmpty()){
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        String searchText = "%" + search.trim() + "%";
        Long unixTimeNow = Timestamp.from(ZonedDateTime.now().toInstant()).getTime();
        Page<MultipleChoiceTest> multipleChoiceTests;
        if (isEnded) {
            multipleChoiceTests = multipleChoiceTestRepository.
                    findEndedMultipleChoiceTestOfClassroomByClassroomId(subjectId,unixTimeNow, searchText, pageable);
        } else {
            multipleChoiceTests = multipleChoiceTestRepository.
                    findNotEndedMultipleChoiceTestOfClassroomByClassroomId(subjectId,unixTimeNow, searchText, pageable);
        }
        Page<MultipleChoiceTestResponse> response = multipleChoiceTests.map(CustomBuilder::buildMultipleChoiceTestResponse);

        response.forEach((item)->{
            Optional<Score> score = scoreRepository.findByMultipleChoiceTestIdAndUserID(item.getId(), myId);
            item.setIsSubmitted(score.isPresent());
        });

        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> updateMultipleChoiceTest(Long testId, UpdateMultipleChoiceTestDTO dto) {
        Optional<MultipleChoiceTest> multipleChoiceTestOp = multipleChoiceTestRepository.findById(testId);
        if(multipleChoiceTestOp.isEmpty()) {
            throw new AppException(ErrorCode.MULTIPLE_CHOICE_NOT_FOUND_ERROR);
        }
        MultipleChoiceTest multipleChoiceTest = multipleChoiceTestOp.get();

        Long unixTimeNow = Timestamp.from(ZonedDateTime.now().toInstant()).getTime();
        if(multipleChoiceTest.getStartDate() < unixTimeNow) {
//            LinkedHashMap<String, String> response = new LinkedHashMap<>();
//            response.put(Constants.ERROR_CODE_KEY, ErrorMessage.MULTIPLE_CHOICE_TEST_UPDATE_STARTED_TEST.getErrorCode());
//            response.put(Constants.MESSAGE_KEY, ErrorMessage.MULTIPLE_CHOICE_TEST_UPDATE_STARTED_TEST.getMessage());
//            return ApiResponse.status(HttpStatus.BAD_REQUEST)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(response);
            throw new AppException(ErrorCode.MULTIPLE_CHOICE_TEST_UPDATE_STARTED_TEST_ERROR);
        }
        if (Objects.nonNull(dto.getTestName())) {
            multipleChoiceTest.setTestName(dto.getTestName());
            modifyUpdateMultipleChoiceTest(multipleChoiceTest);
        }
        if (Objects.nonNull(dto.getTestingTime())) {
            multipleChoiceTest.setTestingTime(dto.getTestingTime());
            modifyUpdateMultipleChoiceTest(multipleChoiceTest);
        }
        if (Objects.nonNull(dto.getTargetScore())) {
            multipleChoiceTest.setTargetScore(dto.getTargetScore());
            modifyUpdateMultipleChoiceTest(multipleChoiceTest);
        }
        if (Objects.nonNull(dto.getStartDate()) && Objects.isNull(dto.getEndDate())) {
            if (dto.getStartDate() < multipleChoiceTest.getEndDate()){
                multipleChoiceTest.setStartDate(dto.getStartDate());
                modifyUpdateMultipleChoiceTest(multipleChoiceTest);
            } else {
               throw new AppException(ErrorCode.MULTIPLE_CHOICE_TEST_DATE_INVALID_ERROR);
            }
        }
        if (Objects.nonNull(dto.getEndDate()) && Objects.isNull(dto.getStartDate())) {
            if (dto.getEndDate() > multipleChoiceTest.getStartDate()){
                multipleChoiceTest.setEndDate(dto.getEndDate());
                modifyUpdateMultipleChoiceTest(multipleChoiceTest);
            } else {
                throw new AppException(ErrorCode.MULTIPLE_CHOICE_TEST_DATE_INVALID_ERROR);
            }
        }
        // Validated in validator
        if (Objects.nonNull(dto.getEndDate()) && Objects.nonNull(dto.getStartDate())) {
            multipleChoiceTest.setEndDate(dto.getEndDate());
            multipleChoiceTest.setStartDate(dto.getStartDate());
            modifyUpdateMultipleChoiceTest(multipleChoiceTest);
        }
        multipleChoiceTest = multipleChoiceTestRepository.save(multipleChoiceTest);
        MultipleChoiceTestResponse response = CustomBuilder.buildMultipleChoiceTestResponse(multipleChoiceTest);
//        mailService.sendTestUpdatedNotificationEmail(multipleChoiceTest);
        return ApiResponse.builder().data(response).build();
    }

    @Override
    public ApiResponse<?> deleteMultipleChoiceTest(Long testId) {
        Optional<MultipleChoiceTest> multipleChoiceTestOp = multipleChoiceTestRepository.findById(testId);
        if(multipleChoiceTestOp.isEmpty()) {
           throw new AppException(ErrorCode.MULTIPLE_CHOICE_NOT_FOUND_ERROR);
        }
        MultipleChoiceTest multipleChoiceTest = multipleChoiceTestOp.get();
        Long unixTimeNow = Timestamp.from(ZonedDateTime.now().toInstant()).getTime();
        if(multipleChoiceTest.getStartDate() < unixTimeNow) {
//            LinkedHashMap<String, String> response = new LinkedHashMap<>();
//            response.put(Constants.ERROR_CODE_KEY, ErrorMessage.MULTIPLE_CHOICE_TEST_DELETE_STARTED_TEST.getErrorCode());
//            response.put(Constants.MESSAGE_KEY, ErrorMessage.MULTIPLE_CHOICE_TEST_DELETE_STARTED_TEST.getMessage());
//            return ApiResponse.status(HttpStatus.BAD_REQUEST)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(response);
            throw new AppException(ErrorCode.MULTIPLE_CHOICE_TEST_DELETE_STARTED_TEST_ERROR);
        }
//        mailService.sendTestDeletedNotificationEmail(multipleChoiceTest);
        multipleChoiceTestRepository.deleteById(testId);
        return ApiResponse.builder().build();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ApiResponse<?> createMultipleChoiceTest(CreateMultipleChoiceTestDTO dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId()).get();
        MultipleChoiceTest multipleChoiceTest = new MultipleChoiceTest();
        multipleChoiceTest.setTestName(dto.getTestName());
        multipleChoiceTest.setDescription(dto.getDescription());
        multipleChoiceTest.setStartDate(dto.getStartDate());
        multipleChoiceTest.setEndDate(dto.getEndDate());
        multipleChoiceTest.setTestingTime(dto.getTestingTime());
        multipleChoiceTest.setSubject(subject);
        multipleChoiceTest.setTargetScore(dto.getTargetScore());
        modifyUpdateMultipleChoiceTest(multipleChoiceTest);
        multipleChoiceTest = multipleChoiceTestRepository.save(multipleChoiceTest);
        List<QuestionResponse> questionsOfTheTest = new ArrayList<>();
        if(Objects.nonNull(dto.getQuestionIds())) {
            questionsOfTheTest =
                    addQuestionsToTestByQuestionId(multipleChoiceTest.getId(),dto.getQuestionIds());
        }
        if(Objects.nonNull(dto.getRandomQuestions())) {
            questionsOfTheTest =
                    addRandomQuestionToTestByQuestionGroupId
                            (multipleChoiceTest.getId(), dto.getRandomQuestions());
        }
        MultipleChoiceTestResponse response = CustomBuilder.buildMultipleChoiceTestResponse(multipleChoiceTest);
        NotificationRequest notificationRequest=NotificationRequest.builder()
                .senderId(multipleChoiceTest.getCreatedBy())
                .receiverId("55420d21-d239-4354-be20-c452c9b72af9")
                .title("Create Multiple Choice Test")
                .idOfTypeNotify(multipleChoiceTest.getId())
                .build();
        notificationClient.sendNotification(notificationRequest);
        // Send notification email to student in this classroom
//        mailService.sendTestCreatedNotificationEmail(dto.getClassroomId(), multipleChoiceTest);
        return ApiResponse.builder().data(response).build();
    }

    private List<QuestionResponse> addRandomQuestionToTestByQuestionGroupId
            (Long id, List<CreateMultipleChoiceTestDTO.Questions> randomQuestions) {
        Optional<MultipleChoiceTest> testOp = multipleChoiceTestRepository.findById(id);
        MultipleChoiceTest multipleChoiceTest = testOp.get();
        List<Question> questions = new ArrayList<>();
        List<QuestionResponse> questionResponses = new ArrayList<>();
        randomQuestions.forEach(value->{
            List<Question> questionsTemp = questionService.
                        getRandomQuestionsByQuestionGroup
                                (value.getQuestionGroupId(), value.getNumberOfQuestion());
            questions.addAll(questionsTemp);
        });
        questions.forEach(question -> {
            TestQuestion testQuestion = TestQuestion.builder()
                    .question(question)
                    .multipleChoiceTest(multipleChoiceTest)
                    .build();
            UserRequest userProfile = identityService.getCurrentUser();
            testQuestion.setCreatedBy(userProfile.getLoginName());
            testQuestionRepository.save(testQuestion);
            questionResponses.add(CustomBuilder.buildQuestionResponse(question,answerRepository.findListAnswerByIdQuestion(question.getId())));
        });
        return new ArrayList<>();
    }

    public List<QuestionResponse> addQuestionsToTestByQuestionId(Long id, List<Long> questionIds) {
        Optional<MultipleChoiceTest> testOp = multipleChoiceTestRepository.findById(id);
        MultipleChoiceTest multipleChoiceTest = testOp.get();
        List<TestQuestion> testQuestions = new ArrayList<>();
        List<QuestionResponse> questionResponses = new ArrayList<>();
        questionIds.forEach((questionId) -> {
            Optional<Question> questionOp = questionRepository.findById(questionId);
            if (questionOp.isEmpty()) {
                throw new AppException(ErrorCode.QUESTION_NOT_FOUND_ERROR);
            }
            Question question = questionOp.get();
            TestQuestion testQuestion = TestQuestion.builder()
                    .question(question)
                    .multipleChoiceTest(multipleChoiceTest)
                    .build();
            UserRequest userProfile = identityService.getCurrentUser();
            testQuestion.setCreatedBy(userProfile.getLoginName());
            testQuestions.add(testQuestion);
            questionResponses.add(CustomBuilder.buildQuestionResponse(question,answerRepository.findListAnswerByIdQuestion(question.getId())));
        });
        // Only save when finding all the questions by list questionId
        testQuestionRepository.saveAll(testQuestions);
        return questionResponses;
    }

    private void modifyUpdateMultipleChoiceTest(MultipleChoiceTest multipleChoiceTest) {
        UserRequest userProfile = identityService.getCurrentUser();
        multipleChoiceTest.setUpdateBy(userProfile.getId());
        multipleChoiceTest.setUpdateDate(Instant.now());
    }
}
