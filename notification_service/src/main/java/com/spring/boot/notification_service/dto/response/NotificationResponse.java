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

        private NotificationMessage message;
        private boolean read;
        private Date createdAt ;
        private Date updatedAt;
        private String createdBy;
        private String updatedBy;
        private Boolean isDeleted ;
}

