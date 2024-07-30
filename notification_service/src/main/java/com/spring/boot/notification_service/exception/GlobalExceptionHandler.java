package com.spring.boot.notification_service.exception;



import com.spring.boot.notification_service.dto.response.APIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<APIResponse> runtimeExceptionHandler(RuntimeException e) {
        log.info("RuntimeException in notify");
        APIResponse response=new APIResponse();
        response.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage() +e.getMessage());
        response.setCode(ErrorCode.UNAUTHENTICATED.getCode());
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<APIResponse> appExceptionHandler(AppException e) {
        APIResponse response=new APIResponse();
        response.setMessage(e.getErrorCode().getMessage());
        response.setCode(e.getErrorCode().getCode());
        return ResponseEntity.status(e.getErrorCode().getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<APIResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String errorKey=e.getFieldError().getDefaultMessage();
        APIResponse response=new APIResponse();
        ErrorCode errorCode= ErrorCode.MESSAGE_KEY_INVALID;
        try{
            errorCode=ErrorCode.valueOf(errorKey);
        }catch (IllegalArgumentException ex){


        }
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(response);
    }
}
