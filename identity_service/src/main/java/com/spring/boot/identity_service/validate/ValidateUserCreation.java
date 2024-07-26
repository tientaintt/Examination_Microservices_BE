package com.spring.boot.identity_service.validate;

import com.spring.boot.identity_service.validate.impl.ValidateUserCreationImpl;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidateUserCreationImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidateUserCreation {
}
