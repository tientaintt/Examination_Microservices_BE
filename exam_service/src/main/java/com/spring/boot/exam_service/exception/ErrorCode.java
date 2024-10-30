package com.spring.boot.exam_service.exception;

import com.spring.boot.exam_service.constants.ErrorMessage;
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
    PERMISSION_DENIED(1009,"Permission denied",HttpStatus.FORBIDDEN),
    DELETED_ADMIN(1010,"Can not delete admin account",HttpStatus.FORBIDDEN),
    WRONG_OLD_PASSWORD(1011, "Old password is incorrect.",HttpStatus.BAD_REQUEST),
    RESET_PASSWORD_NOT_ACCEPTABLE(1012, "Your reset password is not valid",HttpStatus.BAD_REQUEST),
    VERIFY_EMAIL_VERIFIED_BY_ANOTHER_USER(1013,"Email address has been verified by another user.",HttpStatus.BAD_REQUEST),
    VERIFY_NOT_ACCEPTABLE(1014,"Your verification code is not valid.",HttpStatus.NOT_ACCEPTABLE),
    VERIFY_INVALID_STATUS(1015,"User status is not enable to verify.",HttpStatus.BAD_REQUEST),
    FIREBASE_ERROR(1016,"Cannot access firebase database",HttpStatus.INTERNAL_SERVER_ERROR),
    FIREBASE_MESSAGING_ERROR(1017,"Cannot send message to firebase",HttpStatus.INTERNAL_SERVER_ERROR),
    SUBJECT_NOT_FOUND(1018,"Subject not found",HttpStatus.NOT_FOUND),
    CLASS_CODE_DUPLICATE_ERROR(1019, ErrorMessage.SUBJECT_CODE_DUPLICATE.getMessage(), HttpStatus.BAD_REQUEST),
    MULTIPLE_CHOICE_NOT_FOUND_ERROR(1020,ErrorMessage.MULTIPLE_CHOICE_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND),
    MULTIPLE_CHOICE_TEST_UPDATE_STARTED_TEST_ERROR(1021,ErrorMessage.MULTIPLE_CHOICE_TEST_SUBMIT_NOT_STARTED_TEST.getMessage(),HttpStatus.BAD_REQUEST ),
    MULTIPLE_CHOICE_TEST_DATE_INVALID_ERROR(1022, ErrorMessage.MULTIPLE_CHOICE_TEST_DATE_INVALID.getMessage(), HttpStatus.BAD_REQUEST ),
    MULTIPLE_CHOICE_TEST_DELETE_STARTED_TEST_ERROR(1023,ErrorMessage.MULTIPLE_CHOICE_TEST_DELETE_STARTED_TEST.getMessage(),HttpStatus.BAD_REQUEST ),
    QUESTION_NOT_FOUND_ERROR(1024,ErrorMessage.QUESTION_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND),
    QUESTION_GROUP_NOT_FOUND_ERROR(1024,ErrorMessage.QUESTION_GROUP_NOT_FOUND.getMessage(),HttpStatus.NOT_FOUND),
    QUESTION_GROUP_CODE_DUPLICATE(1025,ErrorMessage.QUESTION_GROUP_CODE_DUPLICATE.getMessage(), HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_QUESTION_ERROR(1026,ErrorMessage.NOT_ENOUGH_QUESTION.getMessage(), HttpStatus.BAD_REQUEST),
    SCORE_NOT_FOUND_ERROR(1027,ErrorMessage.SCORE_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND),
    SCORE_TEST_SUBMITTED_ERROR(1028,ErrorMessage.SCORE_TEST_SUBMITTED.getMessage(),HttpStatus.BAD_REQUEST),
    MULTIPLE_CHOICE_TEST_SUBMIT_NOT_STARTED_TEST_ERROR(1029,ErrorMessage. MULTIPLE_CHOICE_TEST_SUBMIT_NOT_STARTED_TEST.getMessage(),HttpStatus.BAD_REQUEST),
    STUDENT_NOT_FOUND_ERROR(1030,ErrorMessage.STUDENT_NOT_FOUND.getMessage(),HttpStatus.NOT_FOUND),
    QUESTION_TYPE_NOT_FOUND_ERROR(1031,"Question type not found",HttpStatus.NOT_FOUND),
    COMMON_FIELD_REQUIRED_ERROR(1034, "%s is required.",HttpStatus.BAD_REQUEST),
    NO_STUDENT_IN_CLASS(1035,"There are no students in this class yet.",HttpStatus.NOT_FOUND),
    CANNOT_READ_FILE(1036,"Cannot read file",HttpStatus.BAD_REQUEST),
    CANNOT_WRITE_FILE(1037,"Cannot write file",HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR);


    ;
   int code;
    String message ;
    HttpStatusCode httpStatusCode;


}
