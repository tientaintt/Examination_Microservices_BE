package com.spring.boot.notification_service.dto.response;

import com.spring.boot.notification_service.entity.NotificationMessage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


import java.util.Date;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
        private String id;
        private String receiverId;
        private String senderId;
        private NotificationMessage message;
        private boolean read;
        private Date createdAt ;

}

