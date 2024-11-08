package com.spring.boot.notification_service.controller;

import com.spring.boot.notification_service.dto.request.NotificationRequest;
import com.spring.boot.notification_service.dto.response.NotificationResponse;
import com.spring.boot.notification_service.entity.Notification;
import com.spring.boot.notification_service.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
@RestController

public class NotificationController {
    SimpMessagingTemplate messagingTemplate;
     NotificationService notificationService;
    @MessageMapping("/create/exam/{classRoomId}")
    public void sendMessage(@Payload String message, @DestinationVariable String classRoomId) {
        log.info("sending message to " + classRoomId);

        log.info(message);
        messagingTemplate.convertAndSend( "/topic/classroom/" + classRoomId, "New exam");
    }


    @PostMapping("/send")

    public void sendNotification(@RequestBody NotificationRequest request) {
        NotificationResponse notification = notificationService.createNotification(request);

        // Gửi thông báo đến người nhận qua WebSocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" +request.getReceiverId(),
                notification
        );
    }
}
