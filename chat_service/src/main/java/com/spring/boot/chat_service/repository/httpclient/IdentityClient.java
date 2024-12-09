package com.spring.boot.chat_service.repository.httpclient;

import com.spring.boot.chat_service.configuration.AuthenticationRequestInterceptor;
import com.spring.boot.chat_service.dto.request.UserRequest;

import com.spring.boot.chat_service.dto.response.APIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="identity-service",url = "${app.services.identity}", configuration = {AuthenticationRequestInterceptor.class})
public interface IdentityClient {
    @GetMapping(value = "/user/my-info",produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<UserRequest> getCurrentUserLogin() ;
    @GetMapping(value = "/users",produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<List<UserRequest>> getAllUsers() ;


}
