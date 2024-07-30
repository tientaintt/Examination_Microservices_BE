package com.spring.boot.identity_service.controller;

import com.spring.boot.identity_service.dto.request.*;
import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;
import com.spring.boot.identity_service.entity.User;
import com.spring.boot.identity_service.exception.AppException;
import com.spring.boot.identity_service.exception.ErrorCode;
import com.spring.boot.identity_service.repository.UserRepository;
import com.spring.boot.identity_service.service.StudentService;
import com.spring.boot.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";
    StudentService studentService;
    UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/email/check")
    APIResponse<?> checkEmail(@RequestParam String email) {
        return userService.checkEmailVerified(email);
    }

    @PutMapping("/reverification")
    APIResponse<?> updateVerifyCode(@RequestParam String userId, @RequestParam String verifyCode) {
        return userService.updateVerifyCode(userId, verifyCode);
    }

    @PutMapping("/updatepasswordverifycode")
    APIResponse<?> updatePasswordCode(@RequestParam String userId, @RequestParam String verifyCode) {
        return userService.updateVerifyCode(userId, verifyCode);
    }

    @PostMapping(value = "/email/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> verifyEmail(@Valid @RequestBody VerificationEmailRequest verificationEmailDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("AAAAAAAAAAA");
        User userProfile = userRepository.findOneByUsername(auth.getName()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        return userService.verifyEmail(userProfile.getId(),verificationEmailDTO);
    }



    @PostMapping(value = "/password/reset/EMAIL:{email-address}", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordDTO,
                                           @PathVariable(value = "email-address") String emailAddress){
        return userService.resetPassword(emailAddress,resetPasswordDTO);
    }
    @PutMapping(value = "/change-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePassword){
        return userService.changePassword(changePassword);
    }
    @PutMapping(value = "/user/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> updateUserprofile(@Valid @RequestBody UpdateUserRequest DTO){
        return userService.updateUserProfile(DTO);
    }
//    UserService userService;
//    @GetMapping
//    APIResponse<List<UserResponse>> getUsers() {
//        return APIResponse.<List<UserResponse>>builder()
//                .data(userService.getUsers())
//                .build();
//    }
//
//    @GetMapping("/{userId}")
//    APIResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
//        return APIResponse.<UserResponse>builder()
//                .data(userService.getUser(userId))
//                .build();
//    }
//
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<?> getAllUsersByUserIds(@RequestParam(required = false) List<String> userIds,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
                                        @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                        @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType){
        return userService.getAllUserByListId(userIds,page,column,size,sortType);
    }
    @GetMapping("/my-info")
    APIResponse<?> getMyInfo() {
        return userService.getCurrentLoggedInUser();
    }

    @GetMapping("")
    APIResponse<?> getUserByEmailVerifiedAndStatus(@RequestParam String userId, @RequestParam boolean status){
        return studentService.getVerifiedStudentByIdAndStatus(userId,status);
    }
//
//    @DeleteMapping("/{userId}")
//    APIResponse<String> deleteUser(@PathVariable String userId) {
//        userService.deleteUser(userId);
//        return APIResponse.<String>builder().data("User has been deleted").build();
//    }
//
//    @PutMapping("/{userId}")
//    APIResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
//        return APIResponse.<UserResponse>builder()
//                .data(userService.updateUser(userId, request))
//                .build();
//    }
}
