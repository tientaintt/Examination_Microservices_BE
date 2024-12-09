package com.spring.boot.notification_service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.event.dto.NotificationEvent;
import com.spring.boot.notification_service.dto.request.NotificationRequest;
import com.spring.boot.notification_service.dto.request.SubjectNotification;
import com.spring.boot.notification_service.dto.request.TestNotification;
import com.spring.boot.notification_service.dto.response.APIResponse;
import com.spring.boot.notification_service.dto.response.ListNotificationResponse;
import com.spring.boot.notification_service.dto.response.NotificationResponse;
import com.spring.boot.notification_service.entity.Notification;
import com.spring.boot.notification_service.entity.NotificationMessage;
import com.spring.boot.notification_service.exception.AppException;
import com.spring.boot.notification_service.exception.ErrorCode;
import com.spring.boot.notification_service.repository.NotificationMessageRepository;
import com.spring.boot.notification_service.repository.NotificationRepository;
import com.spring.boot.notification_service.repository.httpclient.IdentityClient;
import com.spring.boot.notification_service.service.NotificationService;
import com.spring.boot.notification_service.uitls.CustomBuilder;
import com.spring.boot.notification_service.uitls.PageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    NotificationRepository notificationRepository;
    NotificationMessageRepository notificationMessageRepository;
    private final MailServiceImpl mailServiceImpl;
    private final IdentityClient identityClient;
    private final IdentityServiceImpl identityServiceImpl;
    SimpMessagingTemplate messagingTemplate;
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
        log.info("Create Notification");
        NotificationMessage message = new NotificationMessage(
                request.getType(), request.getImage(), request.getTitle(), request.getIdOfTypeNotify());
        Notification notification = Notification.builder()
                .receiverId(request.getReceiverId())
                .senderId(request.getSenderId())
                .message(message)
                .read(false)
                .build();

        Notification notification1 = notificationRepository.save(notification);
        messagingTemplate.convertAndSend(
                "/topic/notifications/" +request.getReceiverId(),
                notification
        );
        log.info("Saved Notification: {}", notification1);
        return NotificationResponse.builder()
                .id(notification1.getId())
                .message(notification1.getMessage())
                .read(notification1.isRead())
                .createdAt(notification1.getCreatedAt())
                .senderId(notification1.getSenderId())
                .receiverId(notification1.getReceiverId())
                .build();

    }

    @Override
    public void listenNotificationDelivery(NotificationEvent event) {
        if (event.getChannel().equals("EMAIL")) {
            ObjectMapper objectMapper = new ObjectMapper();
            if (event.getTemplateCode().equals("CREATE_TEST")) {
                log.info("CREATE TEST");
                TestNotification testNotification = objectMapper.convertValue(event.getParam().get("testNotification"), TestNotification.class);
                List<String> listReceiverId = objectMapper.convertValue(event.getParam().get("listReceiverId"), new TypeReference<List<String>>() {
                });
                String senderId = objectMapper.convertValue(event.getParam().get("senderId"), String.class);
                listReceiverId.stream().forEach(
                        receiverId -> {
                            NotificationRequest notificationRequest = NotificationRequest.builder()
                                    .senderId(senderId)
                                    .receiverId(receiverId)
                                    .title("Have a new exam")
                                    .type("CREATE_TEST")
                                    .idOfTypeNotify(testNotification.getId())
                                    .build();
                            createNotification(notificationRequest);
                        }
                );
                mailServiceImpl.sendEmailTestCreatedNotification(testNotification.getRegisterUserEmails(), testNotification);
            } else if (event.getTemplateCode().equals("EDIT_TEST")) {
                log.info("EDIT TEST");
                TestNotification testNotification = objectMapper.convertValue(event.getParam().get("testNotification"), TestNotification.class);
                List<String> listReceiverId = objectMapper.convertValue(event.getParam().get("listReceiverId"), new TypeReference<List<String>>() {
                });
                String senderId = objectMapper.convertValue(event.getParam().get("senderId"), String.class);
                listReceiverId.stream().forEach(
                        receiverId -> {
                            NotificationRequest notificationRequest = NotificationRequest.builder()
                                    .senderId(senderId)
                                    .receiverId(receiverId)
                                    .title("Update exam")
                                    .type("EDIT_TEST")
                                    .idOfTypeNotify(testNotification.getId())
                                    .build();
                            createNotification(notificationRequest);
                        }
                );
                mailServiceImpl.sendEmailTestUpdatedNotification(testNotification, testNotification.getRegisterUserEmails());

            } else if (event.getTemplateCode().equals("DELETE_TEST")) {
                log.info("DELETE TEST");
                TestNotification testNotification = objectMapper.convertValue(event.getParam().get("testNotification"), TestNotification.class);
                List<String> listReceiverId = objectMapper.convertValue(event.getParam().get("listReceiverId"), new TypeReference<List<String>>() {
                });
                String senderId = objectMapper.convertValue(event.getParam().get("senderId"), String.class);
                listReceiverId.stream().forEach(
                        receiverId -> {
                            NotificationRequest notificationRequest = NotificationRequest.builder()
                                    .senderId(senderId)
                                    .receiverId(receiverId)
                                    .title("Deleted exam")
                                    .type("DELETE_TEST")
                                    .idOfTypeNotify(testNotification.getId())
                                    .build();
                            createNotification(notificationRequest);
                        }
                );
                mailServiceImpl.sendEmailTestDeletedNotification(testNotification.getRegisterUserEmails(), testNotification);
            }
        }
        else {
            if(event.getTemplateCode().equals("ADD_TO_SUBJECT")) {
                ObjectMapper objectMapper = new ObjectMapper();
                SubjectNotification subjectNotification = objectMapper.convertValue(event.getParam().get("subjectNotification"), SubjectNotification.class);
                NotificationRequest notificationRequest = NotificationRequest.builder()
                        .senderId(event.getParam().get("senderId").toString())
                        .receiverId(event.getParam().get("receiverId").toString())
                        .title("Added subject")
                        .type("ADD_TO_SUBJECT")
                        .idOfTypeNotify(subjectNotification.getId())
                        .build();
                createNotification(notificationRequest);
            }
            else if(event.getTemplateCode().equals("REMOVE_FROM_SUBJECT")) {
                ObjectMapper objectMapper = new ObjectMapper();
                SubjectNotification subjectNotification = objectMapper.convertValue(event.getParam().get("subjectNotification"), SubjectNotification.class);
                NotificationRequest notificationRequest = NotificationRequest.builder()
                        .senderId(event.getParam().get("senderId").toString())
                        .receiverId(event.getParam().get("receiverId").toString())
                        .title("Removed subject")
                        .type("REMOVE_FROM_SUBJECT")
                        .idOfTypeNotify(subjectNotification.getId())
                        .build();
                createNotification(notificationRequest);
            }
        }
    }

    @Override
    public APIResponse<?> getAllMyNotifications(int page, String column, int size, String sortType) {
        String userId = identityServiceImpl.getCurrentUser().getId();
        final int[] totalUnread = {0};
        Pageable pageable = PageUtils.createPageable(page, size, "des", "createdAt");
        Page<Notification> notifications = notificationRepository.findByReceiverId(userId, pageable);

        Page<NotificationResponse> notificationResponses = notifications.map(notification -> {
            if(notification.isRead()==false)
                totalUnread[0] = totalUnread[0] +1;
            return CustomBuilder.buildNotification(notification);
        });
        ListNotificationResponse response=ListNotificationResponse.builder()
                .totalUnreadNotifications(totalUnread[0])
                .notifications(notificationResponses)
                .build();
        return APIResponse.builder()
                .data(response)
                .build();
//        List<Criteria> criteria = new ArrayList<>();
//        criteria.add(Criteria.where("receiverId").is(userId));
//        Query query = new Query();
//        query.addCriteria(new Criteria().andOperator(criteria));
//        if(sortType.equals("des")) {
//            query.with(Sort.by(Sort.Order.desc(column)));
//        } else {
//            query.with(Sort.by(Sort.Order.asc(column)));
//        }
//        int startIndex = (page) * size;
//        Long totalResult = mongoTemplate.count(query, Notification.class);
//        query.limit(size);
//        query.skip(startIndex);
//        List<Notification> notifications = mongoTemplate.find(query, Notification.class);
//        List<NotificationResponse> notificationResponses = notifications.stream().map(CustomBuilder::buildNotification).toList();
//        ListWrapper<NotificationResponse> wrapper = ListWrapper.<NotificationResponse>builder()
//                .currentPage(page)
//                .maxResult(totalResult)
//                .data(notificationResponses)
//                .totalPage((totalResult - 1) /  size + 1)
//                .build();
//        return APIResponse.builder()
//                .data(wrapper)
//                .build();
    }

    @Override
    public APIResponse<?> readNotification(String notificationId) {
        Optional<Notification> notification=notificationRepository.findById(notificationId);
        if(notification.isPresent()){
            notification.get().setRead(true);
          Notification result=  notificationRepository.save(notification.get());
          return APIResponse.builder()
                  .data(CustomBuilder.buildNotification(result))
                  .build();
        }else {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
    }
}
