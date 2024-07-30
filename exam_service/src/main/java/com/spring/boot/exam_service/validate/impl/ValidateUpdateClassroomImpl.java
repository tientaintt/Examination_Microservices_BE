package com.spring.boot.exam_service.validate.impl;


import com.spring.boot.exam_service.constants.Constants;
import com.spring.boot.exam_service.dto.request.UpdateClassroomDTO;
import com.spring.boot.exam_service.repository.ClassroomRepository;
import com.spring.boot.exam_service.validate.ValidateUpdateClassroom;
import com.spring.boot.exam_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class ValidateUpdateClassroomImpl implements ConstraintValidator<ValidateUpdateClassroom, UpdateClassroomDTO> {
    private ClassroomRepository classRoomRepository;
    private static final String CLASS_NAME = "className";
    private static final String CLASS_CODE = "classCode";
    private static final String DESCRIPTION = "description";
    private static final String IS_PRIVATE = "is_private";
    private static final String CODE_PREFIX = "classroom_";
    @Override
    public boolean isValid(UpdateClassroomDTO value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean checkClassCode = validateClassCode(value, context);
        return ValidateUtils.isAllTrue(List.of(
                checkClassCode
        ));
    }

    private boolean validateClassCode(UpdateClassroomDTO value, ConstraintValidatorContext context) {

        String classCode = value.getClassCode();
        if(StringUtils.isNoneBlank(classCode)){
            classCode = StringUtils.deleteWhitespace(classCode);
            classCode = classCode.substring(0, Math.min(classCode.length(), Constants.CODE_MAX_LENGTH));
            value.setClassCode(classCode);
        }

//        if(Objects.nonNull(classCode) && classRoomRepository.findByClassCode(classCode).isPresent()){
//            context.buildConstraintViolationWithTemplate(ErrorMessage.CLASS_CODE_DUPLICATE.name())
//                    .addPropertyNode(CLASS_CODE)
//                    .addConstraintViolation();
//            return Boolean.FALSE;
//        }
        return Boolean.TRUE;
    }
}