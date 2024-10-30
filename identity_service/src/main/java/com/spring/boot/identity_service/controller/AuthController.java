package com.spring.boot.identity_service.controller;

import com.nimbusds.jose.JOSEException;
import com.spring.boot.identity_service.dto.request.*;
import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.IntrospectResponse;
import com.spring.boot.identity_service.dto.response.JwtResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;
import com.spring.boot.identity_service.service.AuthenticationService;
import com.spring.boot.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Validated
@RestController
@RequestMapping("")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j
public class AuthController {
    UserService userService;
    AuthenticationService authenticationService;
    @PostMapping(value = "/password/reset/EMAIL:{email-address}", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordDTO,
                                        @PathVariable(value = "email-address") String emailAddress){
        return userService.resetPassword(emailAddress,resetPasswordDTO);
    }
    @PostMapping("/auth/introspect")
    APIResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return APIResponse.<IntrospectResponse>builder().data(result).build();
    }
    @PostMapping("/signup/student")
    APIResponse<JwtResponse> studentSignUp(@RequestBody @Valid UserCreationRequest request) {
        return userService.createUser(request,false,false);
    }
    @PostMapping("/signup/teacher")
    APIResponse<JwtResponse> teacherSignUp(@RequestBody @Valid UserCreationRequest request) {
        return userService.createUser(request,true,false);
    }

    @DeleteMapping(value = "/delete/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public APIResponse<?> deleteUser(@PathVariable(name = "userId") String userId){
        return userService.deleteUser(userId);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> login(@Valid @RequestBody AuthenticationRequest loginVM){
        return userService.login(loginVM);
    }

    @PostMapping(value = "/refresh_token", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> refreshToken(@Valid @RequestBody RefreshRequest refreshTokenDTO){

        return userService.refreshToken(refreshTokenDTO);
    }



    @GetMapping("/check/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> checkRoleStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(String.format("Hello STUDENT %s", auth.getName()), HttpStatus.OK);
    }

    @GetMapping("/check/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> checkRoleTeacher() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(String.format("Hello TEACHER %s", auth.getName()), HttpStatus.OK);
    }
    @GetMapping("/check/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> checkRoleAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return new ResponseEntity<>(String.format("Hello ADMIN %s", auth.getName()), HttpStatus.OK);
    }
}
