package com.spring.boot.identity_service.controller;

import com.spring.boot.identity_service.dto.request.UserCreationRequest;
import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.JwtResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;
import com.spring.boot.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("")
@Slf4j
public class AuthController {
    UserService userService;

    @PostMapping("/signup/student")
    APIResponse<JwtResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return userService.createUser(request,false,false);

    }

}
