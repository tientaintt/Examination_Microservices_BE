package com.spring.boot.exam_service.validate.impl;


import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.request.SubmitMCTestDTO;
import com.spring.boot.exam_service.validate.ValidateSubmitMCTest;
import com.spring.boot.exam_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class ValidateSubmitMCTestImpl implements ConstraintValidator<ValidateSubmitMCTest, SubmitMCTestDTO> {
    public static final String MC_TEST_ID = "multipleChoiceTestId";
    public static final String SUBMITTED_ANSWERS = "submittedAnswers";
    @Override
    public boolean isValid(SubmitMCTestDTO value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean checkMCTestId = validateMCTestId(value, context);
        boolean checkSubmittedAnswers = validateSubmittedAnswers(value, context);
        return ValidateUtils.isAllTrue(List.of(
                checkMCTestId,
                checkSubmittedAnswers
        ));
    }

    private boolean validateSubmittedAnswers(SubmitMCTestDTO value, ConstraintValidatorContext context) {
        if (Objects.isNull(value.getSubmittedAnswers())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(SUBMITTED_ANSWERS)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean validateMCTestId(SubmitMCTestDTO value, ConstraintValidatorContext context) {
        if (Objects.isNull(value.getMultipleChoiceTestId())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(MC_TEST_ID)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
