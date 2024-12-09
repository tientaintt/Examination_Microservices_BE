package com.spring.boot.notification_service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListNotificationResponse {
    Page<NotificationResponse> notifications;
    int totalUnreadNotifications;
}
