package com.spring.boot.notification_service.controller;

import com.spring.boot.event.dto.NotificationEvent;
import com.spring.boot.notification_service.dto.request.NotificationRequest;
import com.spring.boot.notification_service.dto.response.APIResponse;
import com.spring.boot.notification_service.dto.response.NotificationResponse;
import com.spring.boot.notification_service.entity.Notification;
import com.spring.boot.notification_service.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
@RestController

public class NotificationController {
    private static final String DEFAULT_SEARCH = "";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_COLUMN = "id";
    private static final String DEFAULT_SIZE = "12";
    private static final String DEFAULT_SORT_INCREASE = "asc";
     SimpMessagingTemplate messagingTemplate;
     NotificationService notificationService;
     @PostMapping("read/{notification_id}")
     @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
     public APIResponse<?> readNotification(@PathVariable("notification_id") String notificationId){
         return notificationService.readNotification(notificationId);
     }
     @GetMapping(value = "/my")
     public APIResponse<?> getAllMyNotifications(
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_COLUMN) String column,
                                                 @RequestParam(defaultValue = DEFAULT_SIZE) int size,
                                                 @RequestParam(defaultValue = DEFAULT_SORT_INCREASE) String sortType) {
         return notificationService.getAllMyNotifications(page,column,size,sortType);
     }

    @MessageMapping("/create/exam/{classRoomId}")
    public void sendMessage(@Payload String message, @DestinationVariable String classRoomId) {
        log.info("sending message to " + classRoomId);

        log.info(message);
        messagingTemplate.convertAndSend( "/topic/classroom/" + classRoomId, "New exam");
    }


    @PostMapping("/send")
    public void sendNotification(@RequestBody NotificationRequest request) {
        NotificationResponse notification = notificationService.createNotification(request);
        messagingTemplate.convertAndSend(
                "/topic/notifications/" +request.getReceiverId(),
                notification
        );
    }


    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent message){
            notificationService.listenNotificationDelivery(message);
    }
}
