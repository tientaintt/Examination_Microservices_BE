package com.springboot.file_service.exception;




import com.springboot.file_service.dto.response.ApiResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignException(FeignException ex) {
        String responseBody = StandardCharsets.UTF_8.decode(ex.responseBody().get()).toString();
        log.info("Response body: {}",responseBody );
        int httpStatus = ex.status();
        HttpStatus status = HttpStatus.valueOf(httpStatus);
        log.info("HTTP Status: {}", status);
        return ResponseEntity.status(status.value()).body(responseBody);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ApiResponse> runtimeExceptionHandler(RuntimeException e) {
        log.info("RuntimeException in file");
        log.info(e.toString());
        ApiResponse response=new ApiResponse();
        response.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage() +e.getMessage());
        response.setCode(ErrorCode.UNAUTHENTICATED.getCode());
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse> appExceptionHandler(AppException e) {
        ApiResponse response=new ApiResponse();
        response.setMessage(e.getErrorCode().getMessage());
        response.setCode(e.getErrorCode().getCode());
        return ResponseEntity.status(e.getErrorCode().getHttpStatusCode()).body(response);
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    ResponseEntity<ApiResponse> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
////        String errorKey=e.getFieldError().getDefaultMessage();
////        ApiResponse response=new ApiResponse();
////        ErrorCode errorCode= ErrorCode.MESSAGE_KEY_INVALID;
////        try{
////            errorCode=ErrorCode.valueOf(errorKey);
////        }catch (IllegalArgumentException ex){
////
////
////        }
////        response.setCode(errorCode.getCode());
////        response.setMessage(errorCode.getMessage());
////        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(response);
//        log.info("MethodArgumentNotValidException");
//        BindingResult result = e.getBindingResult();
//        ApiResponse response=new ApiResponse();
//        String errorMessageName = result.getAllErrors().get(0).getDefaultMessage();
//        String errorField = Objects.requireNonNull(((FieldError) result.getAllErrors().get(0)).getField());
//        int errorCode ;
//        String message = "";
//        if (ErrorMessage.COMMON_FIELD_REQUIRED.name().equals(errorMessageName)) {
//            errorCode = ErrorCode.COMMON_FIELD_REQUIRED_ERROR.getCode();
//            message = String.format(ErrorMessage.COMMON_FIELD_REQUIRED.getMessage(), errorField);
//        } else {
//
//
//            // Message of ErrorMessage do not have any argument
//            Arrays.asList(ErrorMessage.values()).forEach(
//                    (errorMessage -> {
//                        if (errorMessage.name().equals(errorMessageName)) {
//                            response.setCode(1042);
//                            response.setMessage(errorMessage.getMessage());
//
//                        }
//                    })
//            );
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//
//        response.setCode(errorCode);
//        response.setMessage(message);
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//
//    }
}
