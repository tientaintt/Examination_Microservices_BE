package com.spring.boot.notification_service.uitls;

import com.spring.boot.notification_service.dto.response.NotificationResponse;
import com.spring.boot.notification_service.entity.Notification;

public class CustomBuilder {
    public static NotificationResponse buildNotification(Notification notification) {
        return  NotificationResponse.builder()
                .read(notification.isRead())
                .message(notification.getMessage())
                .id(notification.getId())
                .createdAt(notification.getCreatedAt())
                .createdBy(notification.getCreatedBy())
                .updatedBy(notification.getUpdatedBy())
                .updatedAt(notification.getUpdatedAt())
                .isDeleted(notification.getIsDeleted())
                .build();
    }
}
