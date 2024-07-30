package com.spring.boot.notification_service.controller;

import com.spring.boot.notification_service.service.MailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController("")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class VerifyController {
    MailService mailService;
    @PostMapping(value = "/email/send-verification")
    public ResponseEntity<?> sendVerificationEmail() {
        return mailService.sendVerificationEmail();
    }

    @PostMapping(value = "/password/request-reset/EMAIL:{email-address}")
    public ResponseEntity<?> sendResetPasswordEmail(@PathVariable("email-address") String emailAddress) {
        return mailService.sendResetPasswordEmail(emailAddress);
    }
}