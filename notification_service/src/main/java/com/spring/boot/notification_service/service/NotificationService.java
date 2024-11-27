package com.spring.boot.notification_service.service;

import com.spring.boot.event.dto.NotificationEvent;
import com.spring.boot.notification_service.dto.request.NotificationRequest;
import com.spring.boot.notification_service.dto.response.APIResponse;
import com.spring.boot.notification_service.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest notificationRequest);
    void listenNotificationDelivery(NotificationEvent event);
    APIResponse<?> getAllMyNotifications(int page,String column,int size,String sortType);
}
