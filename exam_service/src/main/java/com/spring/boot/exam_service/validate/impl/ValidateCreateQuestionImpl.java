package com.spring.boot.exam_service.validate.impl;


import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.request.CreateQuestionDTO;
import com.spring.boot.exam_service.validate.ValidateCreateQuestion;
import com.spring.boot.exam_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class ValidateCreateQuestionImpl implements ConstraintValidator<ValidateCreateQuestion, CreateQuestionDTO> {

    private final String CONTENT = "content";
    private final String firstAnswer = "firstAnswer";
    private final String secondAnswer = "secondAnswer";
    private final String thirdAnswer = "thirdAnswer";
    private final String fourthAnswer = "fourthAnswer";

    @Override
    public boolean isValid(CreateQuestionDTO value, ConstraintValidatorContext context) {
        log.info("Validate CreateQuestionDTO: start");
        context.disableDefaultConstraintViolation();
        boolean checkContent = validateContent(value, context);
        boolean checkAnswers = validateAnswers(value, context);

        log.info("Validate CreateQuestionDTO: end");
        return ValidateUtils.isAllTrue(List.of(
                checkContent,
                checkAnswers
        ));
    }

    private boolean validateAnswers(CreateQuestionDTO value, ConstraintValidatorContext context) {
        log.info("Validate Answers of question: start");
        List<Boolean> checks=value.getAnswers().stream().map(answer -> validateAnswer(answer,context,answer.getAnswerContent())).toList();
//        boolean checkFirstAnswer = validateAnswer(value.getFirstAnswer(), context, firstAnswer);
//        boolean checkSecondAnswer = validateAnswer(value.getSecondAnswer(), context, secondAnswer);
//        boolean checkThirdAnswer = validateAnswer(value.getThirdAnswer(), context, thirdAnswer);
//        boolean checkFourthAnswer = validateAnswer(value.getFourthAnswer(), context, fourthAnswer);
        log.info("Validate Answers of question: loading");
        boolean isAllValidAnswer = ValidateUtils.isAllTrue(checks);
        boolean checkOnlyOneCorrect = false;
        if(isAllValidAnswer){
            checkOnlyOneCorrect = validateOnlyOneCorrect(value, context);
        }
        log.info("Validate Answers of question: end");
        return (isAllValidAnswer && checkOnlyOneCorrect);
    }

    private boolean validateOnlyOneCorrect(CreateQuestionDTO value, ConstraintValidatorContext context) {
        log.info("Validate Only One Correct: start");
        List<Boolean> checks=value.getAnswers().stream().map(answer ->  Objects.requireNonNullElse(answer.getIsCorrect(), false)).toList();
        boolean isOnlyOneTrueAnswer = ValidateUtils.isOnlyOneTrue(checks);
        if (!isOnlyOneTrueAnswer){
            context.buildConstraintViolationWithTemplate(ErrorMessage.QUESTION_CREATE_MUST_HAVE_ONE_TRUE_ANSWER.name())
                    .addPropertyNode(CONTENT)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("Validate Only One Correct: end");
        return Boolean.TRUE;
    }

    private boolean validateAnswer(CreateQuestionDTO.Answer value, ConstraintValidatorContext context, String answerName) {
        log.info(String.format("Validate %s of question: start",answerName));
        if(Objects.isNull(value)){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(answerName)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if(StringUtils.isBlank(value.getAnswerContent())){
            context.buildConstraintViolationWithTemplate(ErrorMessage.QUESTION_CREATE_ANSWER_CONTENT_REQUIRED.name())
                    .addPropertyNode(answerName)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if(Objects.isNull(value.getIsCorrect())){
            value.setIsCorrect(Boolean.FALSE);
        }
        log.info(String.format("Validate %s of question: end",answerName));
        return Boolean.TRUE;
    }

    private boolean validateContent(CreateQuestionDTO value, ConstraintValidatorContext context) {
        log.info("Validate content of question: start");
        if (StringUtils.isBlank(value.getContent())){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(CONTENT)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("Validate content of question: end");
        return Boolean.TRUE;
    }
}
