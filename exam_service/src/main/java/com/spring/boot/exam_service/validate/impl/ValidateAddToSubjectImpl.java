package com.spring.boot.exam_service.validate.impl;


import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.request.AddToSubjectDTO;
import com.spring.boot.exam_service.repository.SubjectRepository;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.validate.ValidateAddToSubject;
import com.spring.boot.exam_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class ValidateAddToSubjectImpl implements ConstraintValidator<ValidateAddToSubject, AddToSubjectDTO> {

    private final SubjectRepository subjectRepository;
    private final IdentityService identityService;
    private final String CLASSROOM_ID = "subjectId";
    private final String STUDENT_ID = "studentId";

    @Override
    public boolean isValid(AddToSubjectDTO value, ConstraintValidatorContext context) {
        log.info("Start validate add student to class");
        context.disableDefaultConstraintViolation();
        boolean checkClassroomId = validateClassroomId(value, context);
        boolean checkStudentId = validateStudentId(value, context);
        log.info("End validate add student to class");
        return ValidateUtils.isAllTrue(List.of(
                checkClassroomId,
                checkStudentId
        ));
    }

    private boolean validateStudentId(AddToSubjectDTO value, ConstraintValidatorContext context) {
        log.info("Start check student id");
//        log.info(identityService.getUserVerifiedByIdAndStatus(value.getStudentId().toString(), true).toString());
//        if(identityService.getUserVerifiedByIdAndStatus(value.getStudentId(), true)==null){
//            context.buildConstraintViolationWithTemplate(ErrorMessage.STUDENT_NOT_FOUND.name())
//                    .addPropertyNode(STUDENT_ID)
//                    .addConstraintViolation();
//            return Boolean.FALSE;
//        }
        log.info("End check student id");
        return Boolean.TRUE;
    }

    private boolean validateClassroomId(AddToSubjectDTO value, ConstraintValidatorContext context) {
        log.info("Start check classroom id");

        if(subjectRepository.findSubjectByIdAndStatus(value.getSubjectId(), true).isEmpty()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.SUBJECT_NOT_FOUND.name())
                    .addPropertyNode(CLASSROOM_ID)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }
}
