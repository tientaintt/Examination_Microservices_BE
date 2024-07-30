package com.spring.boot.identity_service.validate.impl;


import com.spring.boot.identity_service.constrant.ErrorMessage;
import com.spring.boot.identity_service.dto.request.VerificationEmailRequest;
import com.spring.boot.identity_service.validate.ValidateUtils;
import com.spring.boot.identity_service.validate.ValidateVerificationEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class ValidateVerificationEmailImpl implements ConstraintValidator<ValidateVerificationEmail, VerificationEmailRequest> {

    private final String CODE = "code";
    @Override
    public boolean isValid(VerificationEmailRequest VerificationEmailRequest, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        Boolean checkCode = validateCode(VerificationEmailRequest, context);

        return ValidateUtils.isAllTrue(List.of(
                checkCode
        ));
    }

    private Boolean validateCode(VerificationEmailRequest VerificationEmailRequest, ConstraintValidatorContext context) {
        if (VerificationEmailRequest.getCode() == null || VerificationEmailRequest.getCode().isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(CODE)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
