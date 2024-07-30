package com.spring.boot.notification_service.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.spring.boot.notification_service.dto.request.NotificationFirebaseRequest;
import com.spring.boot.notification_service.dto.response.APIResponse;
import com.spring.boot.notification_service.exception.AppException;
import com.spring.boot.notification_service.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class FirebaseMessagingService {

    FirebaseMessaging firebaseMessaging;
    public APIResponse<?> sendNotification(NotificationFirebaseRequest notificationFirebaseRequest) {
        log.info("Sending notification to Firebase");
        Notification notification =  Notification.builder()
                .setTitle(notificationFirebaseRequest.getTitle())
                .setBody(notificationFirebaseRequest.getBody())
                .setImage(notificationFirebaseRequest.getImage())
                .build();
        Message message=Message.builder()
                .setTopic("news")
                
                .setNotification(notification)
                .putAllData(notificationFirebaseRequest.getData())
                .build();
        try {
            String resString=firebaseMessaging.sendAsync(message).get();
            log.info("Successfully sent notification to Firebase", resString);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return APIResponse.builder().build();
    }
}
