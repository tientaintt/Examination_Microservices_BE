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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("")
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

    @GetMapping("/user/email/check")
    APIResponse<?> checkEmail(@RequestParam String email) {
        return userService.checkEmailVerified(email);
    }

    @PutMapping("/user/reverification")
    APIResponse<?> updateVerifyCode(@RequestParam String userId, @RequestParam String verifyCode) {
        return userService.updateVerifyCode(userId, verifyCode);
    }

    @PutMapping("/user/updatepasswordverifycode")
    APIResponse<?> updatePasswordCode(@RequestParam String userId, @RequestParam String verifyCode) {
        log.info("Update password verify code for user {}", userId);
        return userService.updateResetPassCode(userId, verifyCode);
    }

    @PostMapping(value = "/user/email/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> verifyEmail(@Valid @RequestBody VerificationEmailRequest verificationEmailDTO){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User userProfile = userRepository.findOneByUsername(auth.getName()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        return userService.verifyEmail(userProfile.getId(),verificationEmailDTO);
    }




    @PutMapping(value = "/user/change-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePassword){
        return userService.changePassword(changePassword);
    }
    @PutMapping(value = "/user/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public APIResponse<?> updateUserprofile(@Valid @RequestBody UpdateUserRequest DTO){
        return userService.updateUserProfile(DTO);
    }
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN','STUDENT')")
    @PutMapping(value = "/user/update/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> updateUserProfileImage(@RequestPart("file") MultipartFile file){
        return  userService.updateImage(file);
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
    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<?> getAllUsersByUserIds(@RequestBody UserIdsRequest userIdsRequest,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
                                        @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                        @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType,
                                        @RequestParam(defaultValue = DEFAULT_SEARCH) String search){
        log.info(userIdsRequest.getUserIds().toString());
        return userService.getAllUserByListId(userIdsRequest.getUserIds(),page,column,size,sortType,search);
    }
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    APIResponse<?> getAllUsers(){

        return userService.getAllUser();
    }
    @GetMapping("/user/my-info")
    APIResponse<?> getMyInfo() {
        return userService.getCurrentLoggedInUser();
    }

    @GetMapping("/user/userIdAndStatus")
    APIResponse<?> getUserByUserIdAndStatus(@RequestParam String userId, @RequestParam boolean status){
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
