package com.spring.boot.notification_service.service;

import com.spring.boot.notification_service.dto.response.APIResponse;
import com.spring.boot.notification_service.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    APIResponse<NotificationResponse> createNotification(String userIdFrom,String userIdTo, String type, String title, String image,Long idOfTypeNotify);
    APIResponse<?> findAllNotificationsByUserId(String userId,int page,String column,int size,String sortType);
}
