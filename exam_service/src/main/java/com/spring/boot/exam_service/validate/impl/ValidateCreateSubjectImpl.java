package com.spring.boot.exam_service.validate.impl;

import com.spring.boot.exam_service.constants.Constants;
import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.request.CreateSubjectDTO;
import com.spring.boot.exam_service.repository.SubjectRepository;

import com.spring.boot.exam_service.validate.ValidateCreateSubject;
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
public class ValidateCreateSubjectImpl implements ConstraintValidator<ValidateCreateSubject, CreateSubjectDTO> {

    private SubjectRepository classRoomRepository;
    private static final String CLASS_NAME = "className";
    private static final String CLASS_CODE = "classCode";
    private static final String DESCRIPTION = "description";
    private static final String IS_PRIVATE = "is_private";
    private static final String CODE_PREFIX = "classroom_";

    @Override
    public boolean isValid(CreateSubjectDTO value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean checkClassName = validateClassName(value, context);
        boolean checkClassCode = validateClassCode(value, context);
        boolean checkDescription = validateDescription(value, context);
        boolean checkIsPrivate = validateIsPrivate(value, context);
        return ValidateUtils.isAllTrue(List.of(
                checkClassName,
                checkClassCode,
                checkDescription,
                checkIsPrivate
        ));
    }

    private boolean validateDescription(CreateSubjectDTO value, ConstraintValidatorContext context) {
        if(Objects.isNull(value.getDescription()) || value.getDescription().isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(DESCRIPTION)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean validateIsPrivate(CreateSubjectDTO value, ConstraintValidatorContext context) {
        if(Objects.isNull(value.getIsPrivate())){
            value.setIsPrivate(true);
        }
        return Boolean.TRUE;
    }

    private boolean validateClassCode(CreateSubjectDTO value, ConstraintValidatorContext context) {
        String classCode = value.getSubjectCode();
        if(Objects.isNull(classCode) || classCode.isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(CLASS_CODE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }


        if(StringUtils.isNoneBlank(classCode)){
            classCode = StringUtils.deleteWhitespace(classCode);
            classCode = classCode.substring(0, Math.min(classCode.length(), Constants.CODE_MAX_LENGTH));
            value.setSubjectCode(classCode);
        }

        if(classRoomRepository.findBySubjectCode(CODE_PREFIX + classCode).isPresent()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.SUBJECT_CODE_DUPLICATE.name())
                    .addPropertyNode(CLASS_CODE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean validateClassName(CreateSubjectDTO value, ConstraintValidatorContext context) {
        if(Objects.isNull(value.getSubjectName()) || value.getSubjectName().isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(CLASS_NAME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }


}