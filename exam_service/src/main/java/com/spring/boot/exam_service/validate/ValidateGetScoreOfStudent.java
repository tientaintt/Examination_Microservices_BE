package com.spring.boot.exam_service.validate;


import com.spring.boot.exam_service.validate.impl.ValidateGetScoreOfStudentImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidateGetScoreOfStudentImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidateGetScoreOfStudent {
    // trường message là bắt buộc, khai báo nội dung sẽ trả về khi k hợp lệ
    String message() default "Get Score Of Student request invalid";
    // Cái này là bắt buộc phải có để Hibernate Validator có thể hoạt động
    Class<?>[] groups() default {};
    // Cái này là bắt buộc phải có để Hibernate Validator có thể hoạt động
    Class<? extends Payload>[] payload() default {};
}
