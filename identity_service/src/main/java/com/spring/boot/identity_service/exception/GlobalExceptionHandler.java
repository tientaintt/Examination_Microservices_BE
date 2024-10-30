package com.spring.boot.identity_service.exception;


import com.spring.boot.identity_service.constrant.Constants;
import com.spring.boot.identity_service.constrant.ErrorMessage;
import com.spring.boot.identity_service.dto.request.UserCreationRequest;
import com.spring.boot.identity_service.dto.response.APIResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<APIResponse> runtimeExceptionHandler(RuntimeException e) {

        log.error("RuntimeException", e);
        APIResponse response=new APIResponse();
        response.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage() +e.getMessage());
        response.setCode(ErrorCode.UNAUTHENTICATED.getCode());
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatusCode()).body(response);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<APIResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex ){
        log.info(ex.getMethod().toString());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info(request.getRequestURI());
        APIResponse response=new APIResponse();
        response.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        response.setCode(ErrorCode.UNAUTHENTICATED.getCode());
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatusCode()).body(response);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<APIResponse> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException exception){

        APIResponse response=new APIResponse();
        response.setMessage(exception.getMessage());
        response.setCode(1036);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(AppException.class)
    ResponseEntity<APIResponse> appExceptionHandler(AppException e) {
        APIResponse response=new APIResponse();
        response.setMessage(e.getErrorCode().getMessage());
        response.setCode(e.getErrorCode().getCode());
        return ResponseEntity.status(e.getErrorCode().getHttpStatusCode()).body(response);
    }
    @ExceptionHandler({BadCredentialsException.class})
     ResponseEntity<APIResponse> handleBadCredentialsException(BadCredentialsException exception) {
        APIResponse response=new APIResponse();
        response.setMessage(ErrorCode.LOGIN_BAD_CREDENTIALS_ERROR.getMessage());
        response.setCode(ErrorCode.LOGIN_BAD_CREDENTIALS_ERROR.getCode());

        return ResponseEntity.status(ErrorCode.LOGIN_BAD_CREDENTIALS_ERROR.getHttpStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<APIResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
//        String errorKey=e.getFieldError().getDefaultMessage();
//        APIResponse response=new APIResponse();
//        ErrorCode errorCode= ErrorCode.MESSAGE_KEY_INVALID;
//        try{
//            errorCode=ErrorCode.valueOf(errorKey);
//        }catch (IllegalArgumentException ex){
//
//
//        }
//        response.setCode(errorCode.getCode());
//        response.setMessage(errorCode.getMessage());
//        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(response);
        log.info("MethodArgumentNotValidException");
        BindingResult result = e.getBindingResult();
        APIResponse response=new APIResponse();
        String errorMessageName = result.getAllErrors().get(0).getDefaultMessage();
        String errorField = Objects.requireNonNull(((FieldError) result.getAllErrors().get(0)).getField());
        int errorCode ;
        String message = "";
        if (ErrorMessage.SIGNUP_LOGIN_NAME_DUPLICATE.name().equals(errorMessageName)) {
            String loginName = ((UserCreationRequest) Objects.requireNonNull(result.getTarget())).getLoginName();
            errorCode = ErrorCode.SIGNUP_LOGIN_NAME_DUPLICATE_ERROR.getCode();
            message = String.format(ErrorMessage.SIGNUP_LOGIN_NAME_DUPLICATE.getMessage(), loginName);
        } else if (ErrorMessage.SIGNUP_EMAIL_ADDRESS_DUPLICATE.name().equals(errorMessageName)) {
            String emailAddress = ((UserCreationRequest) Objects.requireNonNull(result.getTarget())).getEmailAddress();
            errorCode = ErrorCode.SIGNUP_EMAIL_ADDRESS_DUPLICATE_ERROR.getCode();
            message = String.format(ErrorMessage.SIGNUP_EMAIL_ADDRESS_DUPLICATE.getMessage(), emailAddress);
        } else if (ErrorMessage.COMMON_FIELD_REQUIRED.name().equals(errorMessageName)) {
            errorCode = ErrorCode.COMMON_FIELD_REQUIRED_ERROR.getCode();
            message = String.format(ErrorMessage.COMMON_FIELD_REQUIRED.getMessage(), errorField);
        } else {


            // Message of ErrorMessage do not have any argument
            Arrays.asList(ErrorMessage.values()).forEach(
                    (errorMessage -> {
                        if (errorMessage.name().equals(errorMessageName)) {
                            response.setCode(1042);
                            response.setMessage(errorMessage.getMessage());

                        }
                    })
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

            response.setCode(errorCode);
            response.setMessage(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
