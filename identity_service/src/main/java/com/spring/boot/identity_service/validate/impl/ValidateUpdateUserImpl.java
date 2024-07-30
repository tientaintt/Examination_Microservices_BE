package com.spring.boot.identity_service.validate.impl;


import com.spring.boot.identity_service.constrant.Constants;
import com.spring.boot.identity_service.constrant.ErrorMessage;
import com.spring.boot.identity_service.dto.request.UpdateUserRequest;
import com.spring.boot.identity_service.entity.User;
import com.spring.boot.identity_service.repository.UserRepository;
import com.spring.boot.identity_service.validate.ValidateUpdateUser;
import com.spring.boot.identity_service.validate.ValidateUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class ValidateUpdateUserImpl implements ConstraintValidator<ValidateUpdateUser, UpdateUserRequest> {
    private final UserRepository userProfileRepository;
    private static final String DISPLAY_NAME = "displayName";
    private static final String EMAIL_ADDRESS = "emailAddress";

    @Override
    public boolean isValid(UpdateUserRequest value, ConstraintValidatorContext context) {
        log.info("Validate UpdateUserRequest: start");
        context.disableDefaultConstraintViolation();
        boolean checkDisplayName = validateDisplayName(value, context);
        boolean checkEmailAddress = validateEmailAddress(value, context);

        log.info("Validate UpdateUserRequest: end");
        return ValidateUtils.isAllTrue(List.of(
                checkDisplayName,
                checkEmailAddress
        ));
    }

    private boolean validateEmailAddress(UpdateUserRequest value, ConstraintValidatorContext context) {
        log.info(String.format("Validate emailAddress: start"));
        if (StringUtils.isNoneBlank(value.getEmailAddress())) {
            if (StringUtils.isBlank(value.getEmailAddress())) {
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
            Optional<User> userProfileOp = userProfileRepository.findOneByEmailAddressVerified(value.getEmailAddress());
            if (userProfileOp.isPresent()) {
                context.buildConstraintViolationWithTemplate(ErrorMessage.UPDATE_EMAIL_ADDRESS_DUPLICATE.name())
                        .addPropertyNode(EMAIL_ADDRESS)
                        .addConstraintViolation();
                return Boolean.FALSE;
            }
        }
        log.info(String.format("Validate emailAddress: End"));
        return Boolean.TRUE;
    }

    private boolean validateDisplayName(UpdateUserRequest value, ConstraintValidatorContext context) {
        log.info(String.format("Validate displayName: start"));
        if (StringUtils.isNoneBlank(value.getDisplayName())) {
            if (StringUtils.isBlank(value.getDisplayName())) {
                context.buildConstraintViolationWithTemplate(ErrorMessage.COMMON_FIELD_REQUIRED.name())
                        .addPropertyNode(DISPLAY_NAME)
                        .addConstraintViolation();
                return Boolean.FALSE;
            }
        }
        log.info(String.format("Validate displayName: End"));
        return Boolean.TRUE;
    }
}
