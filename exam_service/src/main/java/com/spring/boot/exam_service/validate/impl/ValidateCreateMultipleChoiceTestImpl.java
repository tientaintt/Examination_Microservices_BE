package com.spring.boot.exam_service.validate.impl;


import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.request.CreateMultipleChoiceTestDTO;
import com.spring.boot.exam_service.repository.SubjectRepository;
import com.spring.boot.exam_service.repository.QuestionGroupRepository;
import com.spring.boot.exam_service.validate.ValidateCreateMultipleChoiceTest;
import com.spring.boot.exam_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class ValidateCreateMultipleChoiceTestImpl implements ConstraintValidator<ValidateCreateMultipleChoiceTest, CreateMultipleChoiceTestDTO> {

    private static final String TEST_NAME = "testName";
    private static final String DESCRIPTION = "description";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String TESTING_TIME = "testingTime";
    private static final String CLASSROOM_ID = "subjectId";
    private static final String TARGET_SCORE = "targetScore";
    private static final String QUESTION_IDS = "questionIds";
    private static final String RANDOM_QUESTIONS = "randomQuestions";
    private static final Long unixTimeNow = Timestamp.from(ZonedDateTime.now().toInstant()).getTime();

    private final SubjectRepository subjectRepository;
    private final QuestionGroupRepository questionGroupRepository;


    @Override
    public boolean isValid(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        log.info("Current unix time: " + unixTimeNow);
        boolean checkTestName = validateTestName(value, context);
        boolean checkDescription = validateDescription(value, context);
        boolean checkStartDate = validateStartDate(value, context);
        boolean checkEndDate = validateEndDate(value, context);
        boolean checkTestingTime = validateTestingTime(value, context);
        boolean checkClassroomId = validateClassroomId(value, context);
        boolean checkTargetScore = validateTargetScore(value, context);
        boolean checkQuestionIds = validateQuestionIds(value, context);
        boolean checkRandomQuestions = validateRandomQuestions(value, context);
        boolean checkDuplicateQuestionResource = validateQuestionResource(value, context);
        return ValidateUtils.isAllTrue(List.of(
                checkTestName,
                checkDescription,
                checkStartDate,
                checkEndDate,
                checkTestingTime,
                checkClassroomId,
                checkTargetScore,
                checkQuestionIds,
                checkRandomQuestions,
                checkDuplicateQuestionResource

        ));
    }

    private boolean validateTargetScore(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        if (Objects.isNull(value.getTargetScore())){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(TARGET_SCORE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if (value.getTargetScore() < 0 || value.getTargetScore() >10){
            context.buildConstraintViolationWithTemplate(ErrorMessage.MULTIPLE_CHOICE_TARGET_SCORE_INVALID.name())
                    .addPropertyNode(TARGET_SCORE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean validateDescription(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        log.info("Create multiple choice test: Validate Description: Start");
        if(StringUtils.isBlank(value.getDescription())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(DESCRIPTION)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("Create multiple choice test: Validate Description: End");
        return Boolean.TRUE;
    }

    private boolean validateQuestionResource(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        if((Objects.isNull(value.getQuestionIds()) && Objects.isNull(value.getRandomQuestions()))
            || (Objects.nonNull(value.getQuestionIds()) && Objects.nonNull(value.getRandomQuestions()))) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.MULTIPLE_CHOICE_TEST_QUESTION_SOURCE_INVALID.name())
                    .addPropertyNode(RANDOM_QUESTIONS)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean validateRandomQuestions(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        if(Objects.nonNull(value.getRandomQuestions())) {
            List<CreateMultipleChoiceTestDTO.Questions> randomQuestions = value.getRandomQuestions();
            if (randomQuestions.size()==0) {
                context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                        .addPropertyNode(RANDOM_QUESTIONS)
                        .addConstraintViolation();
                return Boolean.FALSE;
            } else {
                for(CreateMultipleChoiceTestDTO.Questions question : randomQuestions) {
                    if (Objects.isNull(question.getQuestionGroupId())){
                        context.buildConstraintViolationWithTemplate(ErrorMessage.QUESTION_GROUP_REQUIRE.name())
                                .addPropertyNode(RANDOM_QUESTIONS)
                                .addConstraintViolation();
                        return Boolean.FALSE;
                    }
                    if (Objects.isNull(question.getNumberOfQuestion())){
                        context.buildConstraintViolationWithTemplate(ErrorMessage.QUESTION_NUMBER_REQUIRE.name())
                                .addPropertyNode(RANDOM_QUESTIONS)
                                .addConstraintViolation();
                        return Boolean.FALSE;
                    }
                    if(questionGroupRepository.findByIdAndStatus(question.getQuestionGroupId(), true).isEmpty()) {
                        context.buildConstraintViolationWithTemplate(ErrorMessage.QUESTION_GROUP_NOT_FOUND.name())
                                .addPropertyNode(RANDOM_QUESTIONS)
                                .addConstraintViolation();
                        return Boolean.FALSE;
                    }
                }
            }

        }
        return Boolean.TRUE;
    }

    private boolean validateQuestionIds(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context){
        log.info("Create multiple choice test: Validate questionIds: Start");

        if(Objects.nonNull(value.getQuestionIds())) {
            List<Long> questionIds = value.getQuestionIds();
            if (questionIds.size()==0) {
                context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                        .addPropertyNode(QUESTION_IDS)
                        .addConstraintViolation();
                return Boolean.FALSE;
            }
        }
        log.info("Create multiple choice test: Validate questionIds: End");
        return Boolean.TRUE;
    }

    private boolean validateClassroomId(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        log.info("Create multiple choice test: Validate subjectId: Start");
        if(Objects.isNull(value.getSubjectId())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(CLASSROOM_ID)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if(subjectRepository.findById(value.getSubjectId()).isEmpty()) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.SUBJECT_NOT_FOUND.name())
                    .addPropertyNode(CLASSROOM_ID)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("Create multiple choice test: Validate subjectId: End");
        return Boolean.TRUE;
    }

    private boolean validateTestingTime(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        log.info("Create multiple choice test: Validate testingTime: Start");
        if(Objects.isNull(value.getTestingTime())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(TESTING_TIME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if(value.getTestingTime() <= 0) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.MULTIPLE_CHOICE_TESTING_TIME_INVALID.name())
                    .addPropertyNode(TESTING_TIME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("Create multiple choice test: Validate testingTime: End");
        return Boolean.TRUE;
    }

    private boolean validateEndDate(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        log.info("Create multiple choice test: Validate endDate: Start");
        if(Objects.isNull(value.getEndDate())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(END_DATE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if(value.getEndDate() < value.getStartDate()) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.MULTIPLE_CHOICE_TEST_DATE_INVALID.name())
                    .addPropertyNode(END_DATE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("Create multiple choice test: Validate endDate: End");
        return Boolean.TRUE;
    }

    private boolean validateStartDate(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        log.info("Create multiple choice test: Validate startDate: Start");
        if(Objects.isNull(value.getStartDate())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(START_DATE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if(value.getStartDate() < unixTimeNow) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.MULTIPLE_CHOICE_TEST_START_DATE_INVALID.name())
                    .addPropertyNode(START_DATE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("Create multiple choice test: Validate startDate: End");
        return Boolean.TRUE;
    }

    private boolean validateTestName(CreateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        log.info("Create multiple choice test: Validate testName: Start");
        if(StringUtils.isBlank(value.getTestName())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(TEST_NAME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("Create multiple choice test: Validate testName: End");
        return Boolean.TRUE;
    }

}
