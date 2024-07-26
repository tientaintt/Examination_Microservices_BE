package com.spring.boot.identity_service.exception;

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
    UNAUTHENTICATED(1006,"Cannot authenticate user",HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(1007,"Token is invalid",HttpStatus.INTERNAL_SERVER_ERROR),
    PARSER_ERROR(1008,"Cannot parse json",HttpStatus.INTERNAL_SERVER_ERROR),


    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR);
    ;
   int code;
    String message ;
    HttpStatusCode httpStatusCode;


}
