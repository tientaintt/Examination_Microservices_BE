package com.spring.boot.exam_service.validate;


import com.nimbusds.jose.Payload;
import com.spring.boot.exam_service.validate.impl.ValidateUpdateQuestionGroupImpl;
import jakarta.validation.Constraint;


import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidateUpdateQuestionGroupImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidateUpdateQuestionGroup {
    String message() default "Update QuestionGroup request invalid";
    // Cái này là bắt buộc phải có để Hibernate Validator có thể hoạt động
    Class<?>[] groups() default {};
    // Cái này là bắt buộc phải có để Hibernate Validator có thể hoạt động
    Class<? extends Payload>[] payload() default {};
}
