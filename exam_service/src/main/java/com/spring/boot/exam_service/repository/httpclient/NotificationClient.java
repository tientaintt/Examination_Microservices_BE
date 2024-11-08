package com.spring.boot.exam_service.repository.httpclient;

import com.spring.boot.exam_service.configuration.AuthenticationRequestInterceptor;
import com.spring.boot.exam_service.dto.request.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="notification-service",url = "${app.services.notification}", configuration = {
        AuthenticationRequestInterceptor.class})
public interface NotificationClient {

    @PostMapping("/send")
    void sendNotification(@RequestBody NotificationRequest request);
}
