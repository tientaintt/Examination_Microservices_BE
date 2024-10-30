package com.spring.boot.identity_service.exception;

import com.spring.boot.identity_service.constrant.Constants;
import com.spring.boot.identity_service.constrant.ErrorMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public enum ErrorCode {
    MESSAGE_KEY_INVALID(1001,"Error key invalid",HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"User exited",HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username must be at least 3 character",HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004,"Password must be at least 8 characters and max 20 characters",HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"User not exited",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Cannot authenticate user",HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(1007,"Token is invalid",HttpStatus.INTERNAL_SERVER_ERROR),
    PARSER_ERROR(1008,"Cannot parse json",HttpStatus.INTERNAL_SERVER_ERROR),
    PERMISSION_DENIED(1009,"Permission denied",HttpStatus.FORBIDDEN),
    DELETED_ADMIN(1010,"Can not delete admin account",HttpStatus.FORBIDDEN),
    WRONG_OLD_PASSWORD(1011, "Old password is incorrect.",HttpStatus.BAD_REQUEST),
    RESET_PASSWORD_NOT_ACCEPTABLE(1012, "Your reset password is not valid",HttpStatus.BAD_REQUEST),
    VERIFY_EMAIL_VERIFIED_BY_ANOTHER_USER(1013,"Email address has been verified by another user.",HttpStatus.BAD_REQUEST),
    VERIFY_NOT_ACCEPTABLE(1014,"Your verification code is not valid.",HttpStatus.NOT_ACCEPTABLE),
    VERIFY_INVALID_STATUS(1015,"User status is not enable to verify.",HttpStatus.BAD_REQUEST),
    SIGNUP_LOGIN_NAME_DUPLICATE_ERROR(1032,ErrorMessage.SIGNUP_LOGIN_NAME_DUPLICATE.getMessage(), HttpStatus.BAD_REQUEST),
    SIGNUP_EMAIL_ADDRESS_DUPLICATE_ERROR(1033,ErrorMessage.SIGNUP_EMAIL_ADDRESS_DUPLICATE.getMessage(),HttpStatus.BAD_REQUEST),
    COMMON_FIELD_REQUIRED_ERROR(1034, "%s is required.",HttpStatus.BAD_REQUEST),
    LOGIN_BAD_CREDENTIALS_ERROR(1035,"Wrong login name or password.",HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_NOT_VALID(1036,"Refresh token not invalid.",HttpStatus.NOT_FOUND),
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR);

    ;
   int code;
    String message ;
    HttpStatusCode httpStatusCode;


}
