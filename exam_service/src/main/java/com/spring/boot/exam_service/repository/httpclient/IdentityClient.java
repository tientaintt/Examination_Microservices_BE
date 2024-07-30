package com.spring.boot.exam_service.repository.httpclient;

import com.spring.boot.exam_service.configuration.AuthenticationRequestInterceptor;
import com.spring.boot.exam_service.dto.ApiResponse;
import com.spring.boot.exam_service.dto.request.UserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="identity-service",url = "${app.services.identity}", configuration = {AuthenticationRequestInterceptor.class})
public interface IdentityClient {
    @GetMapping(value = "/users",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserRequest> getUserVerifiedAndStatus(
            @RequestParam String userId, @RequestParam boolean status
    );
    @GetMapping(value = "/users/email/check",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<?> checkEmailVerified(

            @RequestParam String email) ;

    @PutMapping(value = "/users/reverification",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<?> updateVerifyCode(
            @RequestParam String userId,
            @RequestParam String verifyCode) ;

    @PutMapping(value = "/users/updatepasswordverifycode",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<?> updateVerifyPasswordCode(
            @RequestParam String userId,
            @RequestParam String verifyCode) ;

    @GetMapping(value = "/users/my-info",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserRequest> getCurrentUserLogin(
            ) ;
    @GetMapping(value = "",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<?> getAllUsersByUserIds(@RequestParam List<String> userIds,
                                        @RequestParam int page,
                                        @RequestParam String column,
                                        @RequestParam int size,
                                        @RequestParam String sortType);
}
