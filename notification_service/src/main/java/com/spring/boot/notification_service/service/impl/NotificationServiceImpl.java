package com.spring.boot.notification_service.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.boot.event.dto.NotificationEvent;
import com.spring.boot.notification_service.dto.request.NotificationRequest;
import com.spring.boot.notification_service.dto.request.TestNotification;
import com.spring.boot.notification_service.dto.response.APIResponse;
import com.spring.boot.notification_service.dto.response.NotificationResponse;
import com.spring.boot.notification_service.dto.response.ListWrapper;
import com.spring.boot.notification_service.entity.Notification;
import com.spring.boot.notification_service.entity.NotificationMessage;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public APIResponse<?> getAllMyNotifications(int page, String column, int size, String sortType) {
        String userId = identityServiceImpl.getCurrentUser().getId();
        Pageable pageable = PageUtils.createPageable(page, size, "des", "createAt");
        Page<Notification> notifications = notificationRepository.findByReceiverId(userId, pageable);
        Page<NotificationResponse> notificationResponses = notifications.map(CustomBuilder::buildNotification);
        return APIResponse.builder()
                .data(notificationResponses)
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
}
