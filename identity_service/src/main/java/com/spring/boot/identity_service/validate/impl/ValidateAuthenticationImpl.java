package com.spring.boot.identity_service.validate.impl;


import com.spring.boot.identity_service.constrant.ErrorMessage;
import com.spring.boot.identity_service.dto.request.AuthenticationRequest;
import com.spring.boot.identity_service.validate.ValidateAuthentication;
import com.spring.boot.identity_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.Objects;

@Slf4j
@AllArgsConstructor
public class ValidateAuthenticationImpl implements ConstraintValidator<ValidateAuthentication, AuthenticationRequest> {
    public static final String LOGIN_NAME = "loginName";
    public static final String PASSWORD = "password";

    /**
     *  Check validate of {@link AuthenticationRequest}
     *
     * @param AuthenticationRequest The {@link AuthenticationRequest} object
     * @param constraintValidatorContext The context
     * @return :
     *  - True if all validate is true,
     *  - False if any validate is false
     */
    @Override
    public boolean isValid(AuthenticationRequest AuthenticationRequest, ConstraintValidatorContext constraintValidatorContext) {
        log.info("Start validate LoginRequestDTO");
        constraintValidatorContext.disableDefaultConstraintViolation();
        boolean checkLoginName = validateLoginName(AuthenticationRequest, constraintValidatorContext);
        boolean checkPassword = validatePassword(AuthenticationRequest, constraintValidatorContext);
        return ValidateUtils.isAllTrue(List.of(
                checkLoginName,
                checkPassword
        ));
    }

    /**
     * Check validate password:
     *  -   Do not null or empty
     *
     * @param value The {@link AuthenticationRequest} object
     * @param context The context
     * @return :
     *  - True if all validate is true,
     *  - False if any validate is false
     */
    private boolean validatePassword(AuthenticationRequest value, ConstraintValidatorContext context) {
        if(Objects.isNull(value.getPassword()) || value.getPassword().isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(PASSWORD)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * Check validate login name:
     *  - Do not null or empty
     *
     * @param value : The {@link AuthenticationRequest} object
     * @param context : The context
     * @return :
     *  - True if all validate is true,
     *  - False if any validate is false
     */
    private boolean validateLoginName(AuthenticationRequest value, ConstraintValidatorContext context) {
        if(Objects.isNull(value.getUsername()) || value.getUsername().isBlank()){
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(LOGIN_NAME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
