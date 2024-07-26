package com.spring.boot.identity_service.constrant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    COMMON_FIELD_REQUIRED(Constants.REQUIRED_PARAMETER, "%s is required."),

    SIGNUP_EMAIL_ADDRESS_INVALID_CHARACTER(Constants.INVALID_PARAMETER, "There are validation errors of email address - Must be made of letters, numbers, contains '@' and/or '_' - '-'. " +
            "Dot isn't allowed at the start and end of the local part. Consecutive dots aren't allowed. a maximum of 64 characters are allowed."),
    SIGNUP_PASSWORD_INVALID_CHARACTER(Constants.INVALID_PARAMETER, "There are validation errors of password - Must be made of letters, numbers, and/or '!' - '~'. Length must be between 8 and 20 chars."),
    SIGNUP_LOGIN_NAME_INVALID_CHARACTER(Constants.INVALID_PARAMETER, "There are validation errors of loginName - Must be made of letter, number, '-', '_', and/or '.' . Length must be between 6 and 16 chars."),
    SIGNUP_LOGIN_NAME_DUPLICATE(Constants.USER_ALREADY_EXISTS, "User with loginName %s already exists."),

    ;
    private String errorCode;
    private String message;

}
