package com.spring.boot.notification_service.service.impl;

import com.spring.boot.notification_service.dto.request.NotificationRequest;
import com.spring.boot.notification_service.dto.response.APIResponse;
import com.spring.boot.notification_service.dto.response.NotificationResponse;
import com.spring.boot.notification_service.entity.Notification;
import com.spring.boot.notification_service.entity.NotificationMessage;
import com.spring.boot.notification_service.repository.NotificationMessageRepository;
import com.spring.boot.notification_service.repository.NotificationRepository;
import com.spring.boot.notification_service.service.NotificationService;
import com.spring.boot.notification_service.uitls.CustomBuilder;
import com.spring.boot.notification_service.uitls.PageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    NotificationRepository notificationRepository;
    NotificationMessageRepository notificationMessageRepository;
    @NonFinal
    PageUtils pageUtils;
//    @Override
//    public APIResponse<NotificationResponse> createNotification(String userIdFrom,String userIdTo, String type, String title, String image,Long idOfTypeNotify) {
//        NotificationMessage notificationMessage = NotificationMessage.builder()
//                .idOfTypeNotify(idOfTypeNotify)
//                .title(title)
//                .type(type)
//                .image(image)
//                .build();
//        NotificationMessage notificationMessageSaved=notificationMessageRepository.save(notificationMessage);
//        Notification notification=Notification.builder()
//                .read(false)
//                .createdAt(new Date())
//                .createdBy(userIdFrom)
//                .isDeleted(false)
//                .message(notificationMessageSaved)
//                .build();
//        Notification notificationSaved= notificationRepository.save(notification);
//        NotificationResponse response= CustomBuilder.buildNotification(notificationSaved);
//        return APIResponse.<NotificationResponse>builder()
//                .data(response)
//                .build();
//    }

    @Override
    public NotificationResponse createNotification(NotificationRequest request) {
            NotificationMessage message = new NotificationMessage(
                    request.getType(), request.getImage(), request.getTitle(), request.getIdOfTypeNotify());
            Notification notification = Notification.builder()
                    .receiverId(request.getReceiverId())
                    .senderId(request.getSenderId())
                    .message(message)
                    .read(false)
                    .build();

            Notification notification1 = notificationRepository.save(notification);
            return NotificationResponse.builder()
                    .id(notification1.getId())
                    .message(notification1.getMessage())
                    .read(notification1.isRead())
                    .createdAt(notification1.getCreatedAt())
                    .createdBy(notification1.getCreatedBy())
                    .updatedAt(notification1.getUpdatedAt())
                    .updatedBy(notification1.getUpdatedBy())
                    .senderId(notification1.getSenderId())
                    .receiverId(notification1.getReceiverId())
                    .build();

    }

    @Override
    public APIResponse<?> findAllNotificationsByUserId(String userId,int page,String column,int size,String sortType) {
//        Pageable pageable=PageUtils.createPageable(page, size, sortType, column);
//        Page<Notification> notifications=notificationRepository.findAllByUserId(userId,pageable);
//        Page<NotificationResponse> notificationResponses=notifications.map(CustomBuilder::buildNotification);
        return APIResponse.builder()

                .build();
    }
}
