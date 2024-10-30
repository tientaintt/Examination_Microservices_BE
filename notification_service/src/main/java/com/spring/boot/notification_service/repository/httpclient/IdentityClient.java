package com.spring.boot.notification_service.repository.httpclient;

import com.google.firebase.remoteconfig.internal.TemplateResponse;
import com.spring.boot.notification_service.configuration.AuthenticationRequestInterceptor;
import com.spring.boot.notification_service.dto.request.UserRequest;
import com.spring.boot.notification_service.dto.response.APIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="identity-service",url = "${app.services.identity}", configuration = {AuthenticationRequestInterceptor.class})
public interface IdentityClient {
    @GetMapping(value = "/user/userIdAndStatus",produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<UserRequest> getUserVerifiedAndStatus(
            @RequestParam String userId, @RequestParam boolean status
    );

    @GetMapping(value = "/user/email/check",produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<?> checkEmailVerified(

            @RequestParam String email) ;

    @PutMapping(value = "/user/reverification",produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<?> updateVerifyCode(
            @RequestParam String userId,
            @RequestParam String verifyCode) ;

    @PutMapping(value = "/user/updatepasswordverifycode",produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<?> updateVerifyPasswordCode(
            @RequestParam String userId,
            @RequestParam String verifyCode) ;

    @GetMapping(value = "/user/my-info",produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<UserRequest> getCurrentUserLogin(
            ) ;
}
