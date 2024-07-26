package com.spring.boot.identity_service.validate.impl;

import com.spring.boot.identity_service.constrant.Constants;
import com.spring.boot.identity_service.constrant.ErrorMessage;
import com.spring.boot.identity_service.dto.request.UserCreationRequest;
import com.spring.boot.identity_service.entity.User;
import com.spring.boot.identity_service.repository.UserRepository;
import com.spring.boot.identity_service.validate.ValidateUserCreation;
import com.spring.boot.identity_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
//Record class automatically creates getter, setter, equals, hashCode and toString methods
// based on the fields declared in the class. The fields in the record class are usually
// declared as final and the value cannot be changed after initialization.
public record ValidateUserCreationImpl(
        UserRepository userRepository) implements ConstraintValidator<ValidateUserCreation, UserCreationRequest> {

    public static final String LOGIN_NAME = "loginName";
    public static final String PASSWORD = "password";
    public static final String DISPLAY_NAME = "displayName";
    public static final String EMAIL_ADDRESS = "emailAddress";
//    public static final String IS_TEACHER = "isTeacher";

    /**
     * check validate of {@link com.spring.boot.identity_service.dto.request.RefreshTokenRequest}
     *
     * @param value   : The {@link UserCreationRequest} object
     * @param context : The context
     * @return :
     * - True if all validate is true,
     * - False if any validate is false
     */
    @Override
    public boolean isValid(UserCreationRequest value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        boolean checkLoginName = validateLoginName(value, context);
        boolean checkPassword = validatePassword(value, context);
        boolean checkDisplayName = validateDisplayName(value, context);
        boolean checkEmailAddress = validateEmailAddress(value, context);
        return ValidateUtils.isAllTrue(List.of(
                checkLoginName,
                checkPassword,
                checkDisplayName,
                checkEmailAddress
        ));
    }

    /**
     * Check validate email address:
     * - Do not null or empty
     * - Check format: ^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$
     * - Check duplicate if this email address has been verified in the database
     *
     * @param value   : The {@link UserCreationRequest} object
     * @param context : The context
     * @return :
     * - True if all validate is true,
     * - False if any validate is false
     */
    private boolean validateEmailAddress(UserCreationRequest value, ConstraintValidatorContext context) {
        if (Objects.isNull(value.getEmailAddress())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(EMAIL_ADDRESS)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if (!value.getEmailAddress().matches(Constants.EMAIL_REGEX)) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.SIGNUP_EMAIL_ADDRESS_INVALID_CHARACTER.name())
                    .addPropertyNode(EMAIL_ADDRESS)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
//        Optional<User> userProfileOp = userRepository.findOneByEmailAddressVerified(value.getEmailAddress());
//        if (userProfileOp.isPresent()){
//            context.buildConstraintViolationWithTemplate(ErrorMessage.SIGNUP_EMAIL_ADDRESS_DUPLICATE.name())
//                    .addPropertyNode(EMAIL_ADDRESS)
//                    .addConstraintViolation();
//            return Boolean.FALSE;
//        }
        return true;
    }

    /**
     * Check validate display name:
     * - Do not null or empty
     *
     * @param value   : The {@link UserCreationRequest} object
     * @param context : The context
     * @return :
     * - True if all validate is true,
     * - False if any validate is false
     */
    private boolean validateDisplayName(UserCreationRequest value, ConstraintValidatorContext context) {
        if (Objects.isNull(value.getDisplayName()) || value.getDisplayName().isBlank()) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(DISPLAY_NAME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return true;
    }

    /**
     * Check validate password:
     * - Do not null or empty
     * - Check format: ^[!-~]{8,20}$
     *
     * @param value   : The {@link UserCreationRequest} object
     * @param context : The context
     * @return :
     * - True if all validate is true,
     * - False if any validate is false
     */
    private boolean validatePassword(UserCreationRequest value, ConstraintValidatorContext context) {
        if (Objects.isNull(value.getPassword())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(PASSWORD)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if (!value.getPassword().matches(Constants.PASSWORD_REGEX)) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.SIGNUP_PASSWORD_INVALID_CHARACTER.name())
                    .addPropertyNode(PASSWORD)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * Check validate login name:
     * - Do not null or empty
     * - Check format: ^([a-zA-Z0-9._-]{4,16}$)
     * - Check duplicate login name in the database
     *
     * @param value   : The {@link UserCreationRequest} object
     * @param context : The context
     * @return :
     * - True if all validate is true,
     * - False if any validate is false
     */
    private boolean validateLoginName(UserCreationRequest value, ConstraintValidatorContext context) {
        if (Objects.isNull(value.getLoginName())) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                    .addPropertyNode(LOGIN_NAME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        if (!value.getLoginName().matches(Constants.LOGIN_NAME_REGEX)) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.SIGNUP_LOGIN_NAME_INVALID_CHARACTER.name())
                    .addPropertyNode(LOGIN_NAME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        Optional<User> userProfileOp = userRepository.findOneByUsername(value.getLoginName());
        if (userProfileOp.isPresent()) {
            context.buildConstraintViolationWithTemplate(ErrorMessage.SIGNUP_LOGIN_NAME_DUPLICATE.name())
                    .addPropertyNode(LOGIN_NAME)
                    .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
