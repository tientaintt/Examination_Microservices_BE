package com.spring.boot.notification_service.controller;

import com.spring.boot.notification_service.dto.request.NotificationFirebaseRequest;
import com.spring.boot.notification_service.dto.response.APIResponse;
import com.spring.boot.notification_service.service.FirebaseMessagingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class FirebaseController {
//    private static final Logger log = LoggerFactory.getLogger(FirebaseController.class);
//    FirebaseMessagingService  firebaseMessagingService;
//
//    @PostMapping("/firebase/send_message")
//    public APIResponse<?> sendMessage(@RequestBody NotificationFirebaseRequest  notificationFirebaseRequest) {
//
//        return firebaseMessagingService.sendNotification(notificationFirebaseRequest);
//    }
}
