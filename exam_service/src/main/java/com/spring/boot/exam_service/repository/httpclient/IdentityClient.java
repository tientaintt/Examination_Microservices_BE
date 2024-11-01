package com.spring.boot.exam_service.repository.httpclient;

import com.spring.boot.exam_service.configuration.AuthenticationRequestInterceptor;
import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.UserIdsDTO;
import com.spring.boot.exam_service.dto.request.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@FeignClient(name="identity-service",url = "${app.services.identity}", configuration = {AuthenticationRequestInterceptor.class})
public interface IdentityClient {

    @GetMapping(value = "/user/userIdAndStatus",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserRequest> getUserVerifiedAndStatus(
            @RequestParam String userId, @RequestParam boolean status
    );
    @GetMapping(value = "/user/email/check",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<?> checkEmailVerified(
            @RequestParam String email) ;

    @PutMapping(value = "/user/reverification",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<?> updateVerifyCode(
            @RequestParam String userId,
            @RequestParam String verifyCode) ;

    @PutMapping(value = "/user/updatepasswordverifycode",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<?> updateVerifyPasswordCode(
            @RequestParam String userId,
            @RequestParam String verifyCode) ;

    @GetMapping(value = "/user/my-info",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserRequest> getCurrentUserLogin() ;
    //In Feign Client, request with @RequestBody is a POST method, so annotation @GetMapping will be ignored
    @GetMapping(value = "/users",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<Page<UserRequest>> getAllUsersByUserIds(@RequestBody UserIdsDTO userIdsRequest,
                                                        @RequestParam int page,
                                                        @RequestParam String column,
                                                        @RequestParam int size,
                                                        @RequestParam String sortType,
                                                        @RequestParam String search);

    @GetMapping(value = "/student/total", produces = MediaType.APPLICATION_JSON_VALUE)
   ApiResponse<?> getAllTotalStudents();
}

