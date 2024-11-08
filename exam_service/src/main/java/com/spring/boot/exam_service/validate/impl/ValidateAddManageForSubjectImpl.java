package com.spring.boot.exam_service.validate.impl;

import com.spring.boot.exam_service.constants.ErrorMessage;
import com.spring.boot.exam_service.dto.request.AddManageForSubjectDTO;
import com.spring.boot.exam_service.dto.request.AddToSubjectDTO;
import com.spring.boot.exam_service.repository.SubjectRepository;
import com.spring.boot.exam_service.service.IdentityService;
import com.spring.boot.exam_service.validate.ValidateAddManageForSubject;
import com.spring.boot.exam_service.validate.ValidateAddToSubject;
import com.spring.boot.exam_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class ValidateAddManageForSubjectImpl implements ConstraintValidator<ValidateAddManageForSubject, AddManageForSubjectDTO> {
    private final SubjectRepository subjectRepository;
    private final IdentityService identityService;
    private final String SUBJECT_ID = "subjectId";
    private final String TEACHER_ID = "teacherId";

    @Override
    public boolean isValid(AddManageForSubjectDTO value, ConstraintValidatorContext context) {
        log.info("Start validate add teacher manager for subject");
        context.disableDefaultConstraintViolation();
        boolean checkClassroomId = validateSubjectId(value, context);
        boolean checkStudentId = validateTeacherId(value, context);
        log.info("End validate add teacher manager for subject");
        return ValidateUtils.isAllTrue(List.of(
                checkClassroomId,
                checkStudentId
        ));
    }

    private boolean validateTeacherId(AddManageForSubjectDTO value, ConstraintValidatorContext context) {
        log.info("Start check teacher id");
//        log.info(identityService.getUserVerifiedByIdAndStatus(value.getStudentId().toString(), true).toString());
//        if(identityService.getUserVerifiedByIdAndStatus(value.getStudentId(), true)==null){
//            context.buildConstraintViolationWithTemplate(ErrorMessage.STUDENT_NOT_FOUND.name())
//                    .addPropertyNode(TEACHER_ID)
//                    .addConstraintViolation();
//            return Boolean.FALSE;
//        }
        log.info("End check teacher id");
        return Boolean.TRUE;
    }

    private boolean validateSubjectId(AddManageForSubjectDTO value, ConstraintValidatorContext context) {
        log.info("Start check classroom id");

        if(subjectRepository.findSubjectByIdAndStatus(value.getSubjectId(), true).isEmpty()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.SUBJECT_NOT_FOUND.name())
                    .addPropertyNode(SUBJECT_ID)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }



}
