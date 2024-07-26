package com.spring.boot.identity_service.controller;

import com.spring.boot.identity_service.dto.request.UserCreationRequest;
import com.spring.boot.identity_service.dto.response.APIResponse;
import com.spring.boot.identity_service.dto.response.UserResponse;
import com.spring.boot.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
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
//    @GetMapping("/my-info")
//    APIResponse<UserResponse> getMyInfo() {
//        return APIResponse.<UserResponse>builder()
//                .data(userService.getMyInfo())
//                .build();
//    }
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
