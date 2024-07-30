package com.spring.boot.exam_service.validate.impl;


import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.request.UpdateMultipleChoiceTestDTO;
import com.spring.boot.exam_service.validate.ValidateUpdateMultipleChoiceTest;
import com.spring.boot.exam_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class ValidateUpdateMultipleChoiceTestImpl implements ConstraintValidator<ValidateUpdateMultipleChoiceTest, UpdateMultipleChoiceTestDTO> {

    private final String TEST_NAME = "testName";

    private static final String DESCRIPTION = "description";

    private final String START_DATE = "startDate";

    private final String END_DATE = "endDate";

    private final String TESTING_TIME = "testingTime";
    private final String TARGET_SCORE = "targetScore";
    private static final Long unixTimeNow = Timestamp.from(ZonedDateTime.now().toInstant()).getTime();

    @Override
    public boolean isValid(UpdateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean checkTestName = validateTestName(value, context);
        boolean checkDescription = validateDescription(value, context);
        boolean checkStartDate = validateStartDate(value, context);
        boolean checkEndDate = validateEndDate(value, context);
        boolean checkTestingTime = validateTestingTime(value, context);
        boolean checkTargetScore = validateTargetScore(value, context);
        return ValidateUtils.isAllTrue(List.of(
                checkTestName,
                checkDescription,
                checkStartDate,
                checkEndDate,
                checkTestingTime,
                checkTargetScore
        ));
    }

    private boolean validateTargetScore(UpdateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        if (Objects.nonNull(value.getTargetScore())){
            if (value.getTargetScore() < 0 || value.getTargetScore() >10){
                context.buildConstraintViolationWithTemplate(ErrorMessage.MULTIPLE_CHOICE_TARGET_SCORE_INVALID.name())
                        .addPropertyNode(TARGET_SCORE)
                        .addConstraintViolation();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private boolean validateDescription(UpdateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        if(Objects.nonNull(value.getDescription())) {
            if (value.getDescription().isBlank()) {
                context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                        .addPropertyNode(DESCRIPTION)
                        .addConstraintViolation();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private boolean validateTestingTime(UpdateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        if(Objects.nonNull(value.getTestingTime())) {
            if(value.getTestingTime() <= 0) {
                context.buildConstraintViolationWithTemplate(ErrorMessage.MULTIPLE_CHOICE_TESTING_TIME_INVALID.name())
                        .addPropertyNode(TESTING_TIME)
                        .addConstraintViolation();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private boolean validateEndDate(UpdateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        if(Objects.nonNull(value.getEndDate()) && Objects.nonNull(value.getStartDate())) {
            if(value.getEndDate() < value.getStartDate()) {
                context.buildConstraintViolationWithTemplate(ErrorMessage.MULTIPLE_CHOICE_TEST_DATE_INVALID.name())
                        .addPropertyNode(END_DATE)
                        .addConstraintViolation();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private boolean validateStartDate(UpdateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        if(Objects.nonNull(value.getStartDate())) {
            if(value.getStartDate() < unixTimeNow) {
                context.buildConstraintViolationWithTemplate(ErrorMessage.MULTIPLE_CHOICE_TEST_START_DATE_INVALID.name())
                        .addPropertyNode(START_DATE)
                        .addConstraintViolation();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private boolean validateTestName(UpdateMultipleChoiceTestDTO value, ConstraintValidatorContext context) {
        if(Objects.nonNull(value.getTestName())) {
            if (value.getTestName().isBlank()) {
                context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                        .addPropertyNode(TEST_NAME)
                        .addConstraintViolation();
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
