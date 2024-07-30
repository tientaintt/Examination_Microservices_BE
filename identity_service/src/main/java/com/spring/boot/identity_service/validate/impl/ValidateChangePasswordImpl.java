package com.spring.boot.identity_service.validate.impl;


import com.spring.boot.identity_service.constrant.Constants;
import com.spring.boot.identity_service.constrant.ErrorMessage;
import com.spring.boot.identity_service.dto.request.ChangePasswordRequest;
import com.spring.boot.identity_service.validate.ValidateChangePassword;
import com.spring.boot.identity_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class ValidateChangePasswordImpl implements ConstraintValidator<ValidateChangePassword, ChangePasswordRequest> {

    public static final String OLD_PASSWORD = "oldPassword";
    public static final String NEW_PASSWORD = "newPassword";
    @Override
    public boolean isValid(ChangePasswordRequest value, ConstraintValidatorContext context) {
        log.info("Start validate ChangePasswordDTO");
        context.disableDefaultConstraintViolation();
        boolean checkOldPassword = validateOldPassword(value, context);
        boolean checkNewPassword = validateNewPassword(value, context);
        return ValidateUtils.isAllTrue(List.of(
                checkOldPassword,
                checkNewPassword
        ));
    }

    private boolean validateNewPassword(ChangePasswordRequest value, ConstraintValidatorContext context) {
        if(Objects.isNull(value.getNewPassword())){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(NEW_PASSWORD)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if(!value.getNewPassword().matches(Constants.PASSWORD_REGEX)){
            context.buildConstraintViolationWithTemplate(ErrorMessage.SIGNUP_PASSWORD_INVALID_CHARACTER.name())
                    .addPropertyNode(NEW_PASSWORD)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean validateOldPassword(ChangePasswordRequest value, ConstraintValidatorContext context) {
        if(Objects.isNull(value.getOldPassword()) || value.getOldPassword().isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(OLD_PASSWORD)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
