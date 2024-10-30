package com.spring.boot.notification_service.service;


import com.spring.boot.notification_service.dto.request.MultipleChoiceTestRequest;
import com.spring.boot.notification_service.dto.request.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

public interface MailService {
    void sendTestCreatedNotificationEmail(Long subjectId, MultipleChoiceTestRequest multipleChoiceTest);

    void sendTestDeletedNotificationEmail(MultipleChoiceTestRequest multipleChoiceTest);
    void sendTestUpdatedNotificationEmail(MultipleChoiceTestRequest multipleChoiceTest);

    @Async
    ResponseEntity<?> sendVerificationEmail();
    @Async
    ResponseEntity<?> sendResetPasswordEmail(String emailAddress);
}
