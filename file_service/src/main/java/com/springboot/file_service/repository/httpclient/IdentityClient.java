package com.springboot.file_service.repository.httpclient;

import com.springboot.file_service.configuration.AuthenticationRequestInterceptor;
import com.springboot.file_service.dto.request.UserRequest;
import com.springboot.file_service.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="identity-service",url = "${app.services.identity}", configuration = {AuthenticationRequestInterceptor.class})
public interface IdentityClient {
    @GetMapping(value = "/user/my-info",produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<UserRequest> getCurrentUserLogin() ;
}
