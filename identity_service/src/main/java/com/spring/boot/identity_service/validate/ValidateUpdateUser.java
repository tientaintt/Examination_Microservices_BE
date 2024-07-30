package com.spring.boot.identity_service.validate;

import com.nimbusds.jose.Payload;
import com.spring.boot.identity_service.validate.impl.ValidateUpdateUserImpl;
import jakarta.validation.Constraint;


import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidateUpdateUserImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidateUpdateUser {
    String message() default "Update UserProfile request invalid";
    // Cái này là bắt buộc phải có để Hibernate Validator có thể hoạt động
    Class<?>[] groups() default {};
    // Cái này là bắt buộc phải có để Hibernate Validator có thể hoạt động
    Class<? extends Payload>[] payload() default {};
}
