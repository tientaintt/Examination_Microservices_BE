package com.spring.boot.identity_service.validate.impl;

import com.spring.boot.identity_service.constrant.Constants;
import com.spring.boot.identity_service.constrant.ErrorMessage;
import com.spring.boot.identity_service.dto.request.ResetPasswordRequest;
import com.spring.boot.identity_service.validate.ValidateResetPassword;
import com.spring.boot.identity_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class ValidateResetPasswordImpl implements ConstraintValidator<ValidateResetPassword, ResetPasswordRequest> {

    public static final String PASSWORD = "password";
    public static final String CODE = "code";

    @Override
    public boolean isValid(ResetPasswordRequest value, ConstraintValidatorContext context) {
        log.info("Start validate SignUpRequestDTO");
        context.disableDefaultConstraintViolation();
        boolean checkPassword = validatePassword(value, context);
        boolean checkResetPasswordCode = validateResetPasswordCode(value, context);
        return ValidateUtils.isAllTrue(List.of(
                checkPassword,
                checkResetPasswordCode
        ));
    }

    private boolean validateResetPasswordCode(ResetPasswordRequest value, ConstraintValidatorContext context) {
        if(Objects.isNull(value.getCode()) || value.getCode().isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(CODE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean validatePassword(ResetPasswordRequest value, ConstraintValidatorContext context) {
        if(Objects.isNull(value.getPassword())){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(PASSWORD)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if(!value.getPassword().matches(Constants.PASSWORD_REGEX)){
            context.buildConstraintViolationWithTemplate(ErrorMessage.SIGNUP_PASSWORD_INVALID_CHARACTER.name())
                    .addPropertyNode(PASSWORD)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
