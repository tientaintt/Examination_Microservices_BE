package com.spring.boot.exam_service.validate.impl;


import com.spring.boot.exam_service.constants.Constants;
import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.request.CreateQuestionGroupDTO;
import com.spring.boot.exam_service.repository.ClassroomRepository;
import com.spring.boot.exam_service.repository.QuestionGroupRepository;
import com.spring.boot.exam_service.validate.ValidateCreateQuestionGroup;
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
public class ValidateCreateQuestionGroupImpl implements ConstraintValidator<ValidateCreateQuestionGroup, CreateQuestionGroupDTO> {

    private final QuestionGroupRepository questionGroupRepository;
    private final ClassroomRepository classroomRepository;
    private static final String CODE = "code";
    private static final String NAME = "name";
    private static final String CLASSROOM_ID = "classroomId";
    private static final String CODE_PREFIX = "group_";
    private static final String DESCRIPTION = "description";
    @Override
    public boolean isValid(CreateQuestionGroupDTO value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean checkCode = validateQuestionGroupCode(value, context);
        boolean checkName = validateQuestionGroupName(value, context);
        boolean checkDesc = validateDescription(value, context);
        boolean checkClassroomId = validateQuestionGroupClassroomId(value, context);
        return ValidateUtils.isAllTrue(List.of(
                checkCode,
                checkName,
                checkDesc,
                checkClassroomId
        ));
    }

    private boolean validateDescription(CreateQuestionGroupDTO value, ConstraintValidatorContext context) {
        log.info("Start validate DESCRIPTION when creating question group");
        if(Objects.isNull(value.getDescription()) || value.getDescription().isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(DESCRIPTION)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("End validate DESCRIPTION when creating question group");
        return Boolean.TRUE;
    }

    private boolean validateQuestionGroupClassroomId(CreateQuestionGroupDTO value, ConstraintValidatorContext context) {
        log.info("Start validate classroomId when creating question group");
        if(Objects.isNull(value.getClassroomId())){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(CLASSROOM_ID)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if(classroomRepository.findById(value.getClassroomId()).isEmpty()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.CLASSROOM_NOT_FOUND.name())
                    .addPropertyNode(CLASSROOM_ID)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("End validate classroomId when creating question group");
        return Boolean.TRUE;
    }

    private boolean validateQuestionGroupName(CreateQuestionGroupDTO value, ConstraintValidatorContext context) {
        log.info("Start validate name when creating question group");
        if(Objects.isNull(value.getName()) || value.getName().isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(NAME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("End validate name when creating question group");
        return Boolean.TRUE;
    }

    private boolean validateQuestionGroupCode(CreateQuestionGroupDTO value, ConstraintValidatorContext context) {
        log.info("Start validate code when creating question group");
        String code = value.getCode();
        if(Objects.isNull(code) || code.isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(CODE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }

        if(StringUtils.isNoneBlank(code)){
            code = StringUtils.deleteWhitespace(code);
            code = code.substring(0, Math.min(code.length(), Constants.CODE_MAX_LENGTH));
            value.setCode(code);
        }

        if(questionGroupRepository.findByCode(CODE_PREFIX + code).isPresent()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.QUESTION_GROUP_CODE_DUPLICATE.name())
                    .addPropertyNode(CODE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        log.info("End validate code when creating question group");
        return Boolean.TRUE;
    }
}
