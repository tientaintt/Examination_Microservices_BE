package com.spring.boot.notification_service.service;


import com.spring.boot.notification_service.dto.request.TestNotification;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

public interface MailService {
    void sendTestCreatedNotificationEmail(Long subjectId, TestNotification multipleChoiceTest);

    void sendTestDeletedNotificationEmail(TestNotification multipleChoiceTest);
    void sendTestUpdatedNotificationEmail(TestNotification multipleChoiceTest);

    @Async
    ResponseEntity<?> sendVerificationEmail();
    @Async
    ResponseEntity<?> sendResetPasswordEmail(String emailAddress);
}
