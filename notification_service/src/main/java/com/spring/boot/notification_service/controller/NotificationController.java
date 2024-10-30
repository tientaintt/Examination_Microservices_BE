package com.spring.boot.notification_service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class NotificationController {
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/create/exam/{classRoomId}")
    public void sendMessage(@Payload String message, @DestinationVariable String classRoomId) {
        log.info("sending message to " + classRoomId);

        log.info(message);
        messagingTemplate.convertAndSend( "/topic/classroom/" + classRoomId, "New exam");
    }
}
